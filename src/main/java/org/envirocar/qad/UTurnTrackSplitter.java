package org.envirocar.qad;

import org.envirocar.qad.model.Track;
import org.envirocar.qad.utils.AngleUtils;
import org.envirocar.qad.utils.GeometryException;
import org.envirocar.qad.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UTurnTrackSplitter extends AbstractTrackSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(UTurnTrackSplitter.class);
    private final int windowSize;
    private final double bufferSize;
    private final double minAngleDeviation;

    public UTurnTrackSplitter(AlgorithmParameters parameters) {
        this.windowSize = parameters.getUTurn().getWindowSize();
        this.bufferSize = parameters.getUTurn().getBufferSize();
        this.minAngleDeviation = parameters.getUTurn().getMinAngleDeviation();
    }

    @Override
    protected int findSplitIndex(Track track) {
        int size = track.size();
        for (int idx = 1; idx < size - 1; ++idx) {
            try {
                double heading1 = track.getHeading(idx);
                Geometry buffer = GeometryUtils.buffer(track.getGeometry(idx), bufferSize);
                int windowEnd = Math.min(size - 1, 1 + idx + windowSize);
                for (int succ = idx + 1; succ < windowEnd; ++succ) {
                    if (buffer.contains(track.getGeometry(succ))) {
                        double heading2 = track.getHeading(succ);
                        if (isOppositeDirection(heading1, heading2)) {
                            return findMaxHeadingDeviation(track, idx, succ);
                        }
                    }
                }
            } catch (GeometryException e) {
                LOG.error("Could not buffer geometry", e);
            }
        }
        return -1;
    }

    private int findMaxHeadingDeviation(Track track, int begin, int end) {
        double maxDeviation = 0;
        int maxIndex = 0;
        double heading = track.getHeading(begin);
        for (int i = begin; i <= end; ++i) {
            double thisHeading = heading;
            heading = track.getHeading(i + 1);
            double deviation = Math.abs(AngleUtils.deviation(thisHeading, heading));
            if (maxDeviation < deviation) {
                maxDeviation = deviation;
                maxIndex = i + 1;
            }
        }
        return maxIndex;
    }

    private boolean isOppositeDirection(double heading, double heading2) {
        return Math.abs(AngleUtils.deviation(heading, heading2)) >= minAngleDeviation;
    }
}
