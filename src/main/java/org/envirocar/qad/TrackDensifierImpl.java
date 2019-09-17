package org.envirocar.qad;

import org.envirocar.qad.model.Measurement;
import org.envirocar.qad.model.Track;
import org.envirocar.qad.model.Values;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TrackDensifierImpl implements TrackDensifier {
    private static final BigDecimal KILOMETER_PER_HOUR_PER_METER_PER_SECOND = BigDecimal.valueOf(3.6);
    private final int numPoints;

    public TrackDensifierImpl(@Value("${qad.densify.numPoints}") int numPoints) {
        this.numPoints = numPoints;
    }

    @Override
    public Track densify(Track track) {
        return densify(track, numPoints);
    }

    @Override
    public Track densify(Track track, int numPoints) {
        List<Measurement> measurements = new ArrayList<>(track.size() + numPoints * (track.size() - 1));
        Measurement prev = null;
        for (Measurement curr : track.getMeasurements()) {
            measurements.add(curr);
            if (prev == null) {
                prev = curr;
            } else {
                measurements.addAll(interpolateBetween(prev, curr, numPoints));
            }
        }
        return new Track(track.getId(), measurements);
    }

    private List<Measurement> interpolateBetween(Measurement m1, Measurement m2, int numPoints) {

        LineSegment lineSegment = new LineSegment(m1.getGeometry().getCoordinate(),
                                                  m2.getGeometry().getCoordinate());

        BigDecimal distance = BigDecimal.valueOf(lineSegment.getLength());
        BigDecimal distanceDeltaSum = BigDecimal.ZERO;
        Measurement prev = m1;
        GeometryFactory geometryFactory = m1.getGeometry().getFactory();

        if (distance.signum() <= 0) {
            return Collections.emptyList();
        }
        ArrayList<Measurement> measurements = new ArrayList<>(numPoints);
        for (int i = 1; i <= numPoints; ++i) {
            BigDecimal fraction = BigDecimal.valueOf(i).divide(BigDecimal.valueOf(numPoints).add(BigDecimal.ONE),
                                                               RoundingMode.DOWN);
            Instant time = Interpolate.linear(m1.getTime(), m2.getTime(), fraction);
            Values values = Values.interpolate(m1.getValues(), m2.getValues(), fraction);

            distanceDeltaSum = prev.getValues().getSpeed()
                                   .divide(KILOMETER_PER_HOUR_PER_METER_PER_SECOND, RoundingMode.HALF_DOWN)
                                   .multiply(BigDecimals.create(Duration.between(prev.getTime(), time)))
                                   .add(distanceDeltaSum);

            Point geometry = geometryFactory.createPoint(lineSegment.pointAlong(
                    distanceDeltaSum.divide(distance, RoundingMode.HALF_DOWN).doubleValue()));

            String id = String.format("%s_%s_%d", m1.getId(), m2.getId(), i);
            Measurement m = new Measurement(id, geometry, time, values);

            measurements.add(m);
            prev = m;
        }
        return measurements;
    }

}
