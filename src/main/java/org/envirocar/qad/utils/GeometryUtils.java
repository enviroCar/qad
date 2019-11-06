package org.envirocar.qad.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.envirocar.qad.model.Feature;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.linearref.LocationIndexedLine;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.n52.jackson.datatype.jts.JtsModule;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.Arrays;
import java.util.stream.IntStream;

public class GeometryUtils {
    private GeometryUtils() {}

    public static LineString flatten(MultiLineString geometry) {
        Coordinate[] coordinates = IntStream.range(0, geometry.getNumGeometries())
                                            .mapToObj(geometry::getGeometryN)
                                            .map(Geometry::getCoordinates)
                                            .flatMap(Arrays::stream)
                                            .toArray(Coordinate[]::new);
        return geometry.getFactory().createLineString(coordinates);
    }

    /**
     * Calculates the overall heading of the {@link LineString} in degrees.
     *
     * @param line The {@link LineString}.
     * @return The heading in degrees.
     */
    public static double heading(LineString line) {
        int n = line.getNumPoints();
        if (n < 2) {
            return 0.0d;
        }
        Coordinate c0 = line.getCoordinateN(0);
        Coordinate c1 = line.getCoordinateN(n - 1);
        return heading(c0, c1);
    }

    /**
     * Calculates the heading of the {@linkplain Coordinate coordinates} in degrees.
     *
     * @param c0 The first {@link Coordinate}.
     * @param c1 The second {@link Coordinate}.
     * @return The heading in degrees.
     */
    public static double heading(Coordinate c0, Coordinate c1) {
        return heading(new LineSegment(c0, c1));
    }

    /**
     * Calculates the heading of the {@link LineSegment} in degrees.
     *
     * @param segment The {@link LineSegment}.
     * @return The heading in degrees.
     */
    public static double heading(LineSegment segment) {
        return AngleUtils.normalize(Math.toDegrees(segment.angle()));
    }

    /**
     * Returns the distance between the {@linkplain Point points} in meters.
     *
     * @param p0 The first {@link Point}.
     * @param p1 The second {@link Point}.
     * @return The length in meters.
     */
    public static double distance(Point p0, Point p1) {
        return distance(p0.getCoordinate(), p1.getCoordinate());
    }

    /**
     * Returns the length of the {@link LineSegment} in meters.
     *
     * @param ls The {@link LineSegment}.
     * @return The length in meters.
     */
    public static double length(LineSegment ls) {
        return distance(ls.p0, ls.p1);
    }

    /**
     * Returns the length of the {@link CoordinateSequence} in meters.
     *
     * @param sequence The {@link CoordinateSequence}.
     * @return The length in meters.
     */
    public static double length(CoordinateSequence sequence) {
        int n = sequence.size();
        if (n <= 1) {
            return 0.0;
        }
        double len = 0.0;
        double x0 = sequence.getOrdinate(0, 0);
        double y0 = sequence.getOrdinate(0, 1);
        for (int i = 1; i < n; i++) {
            double x1 = sequence.getOrdinate(i, 0);
            double y1 = sequence.getOrdinate(i, 1);
            len += distance(x0, y0, x1, y1);
            x0 = x1;
            y0 = y1;
        }
        return len;
    }

    public static LineString snapLineToLine(LineString source, LineString target) {
        LocationIndexedLine locationIndexedLine = new LocationIndexedLine(target);
        Coordinate[] coords = new Coordinate[source.getNumPoints()];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = locationIndexedLine.extractPoint(locationIndexedLine.project(source.getCoordinateN(i)));
        }

        return source.getFactory().createLineString(coords);
    }

    public static double simplifiedLength(LineString ls) {
        if (ls.getNumPoints() < 2) {
            return 0.0d;
        }
        return distance(ls.getCoordinateN(0), ls.getCoordinateN(ls.getNumPoints() - 1));

    }

    /**
     * Returns the length of the {@link LineString} in meters.
     *
     * @param lineString The {@link LineString}.
     * @return The length in meters.
     */
    public static double length(LineString lineString) {
        return length(lineString.getCoordinateSequence());
    }

    /**
     * Returns the distance between the {@linkplain Coordinate coordinates} in meters.
     *
     * @param c0 The first {@link Coordinate}.
     * @param c1 The second {@link Coordinate}.
     * @return The length in meters.
     */
    public static double distance(Coordinate c0, Coordinate c1) {
        return distance(c0.getX(), c0.getY(), c1.getX(), c1.getY());
    }

    private static double distance(double lon0, double lat0, double lon1, double lat1) {
        double sin1 = Math.sin(Math.toRadians(lat1 - lat0) / 2);
        double sin2 = Math.sin(Math.toRadians(lon1 - lon0) / 2);
        double cos1 = Math.cos(Math.toRadians(lat0));
        double cos2 = Math.cos(Math.toRadians(lat1));
        double a = sin1 * sin1 + cos1 * cos2 * sin2 * sin2;
        return 2 * 6371000 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /**
     * Buffers the {@link Geometry} by first transforming it into a projected coordinate reference system and then back
     * to WGS 84.
     *
     * @param geometry The {@link Geometry}.
     * @param meters   The buffer width.
     * @return The buffered {@link Geometry}.
     * @throws GeometryException If the {@link Geometry} is not using WGS84.
     * @see Geometry#buffer(double)
     */
    public static Geometry buffer(Geometry geometry, double meters) throws GeometryException {
        return buffer(geometry, meters, BufferParameters.DEFAULT_QUADRANT_SEGMENTS, BufferParameters.CAP_ROUND);
    }

    /**
     * Buffers the {@link Geometry} by first transforming it into a projected coordinate reference system and then back
     * to WGS 84.
     *
     * @param geometry         The {@link Geometry}.
     * @param meters           The buffer width.
     * @param quadrantSegments The number of line segments used to represent a quadrant of a circle.
     * @return The buffered {@link Geometry}.
     * @throws GeometryException If the {@link Geometry} is not using WGS84.
     * @see Geometry#buffer(double, int)
     */
    public static Geometry buffer(Geometry geometry, double meters, int quadrantSegments) throws GeometryException {
        return buffer(geometry, meters, quadrantSegments, BufferParameters.CAP_ROUND);
    }

    /**
     * Buffers the {@link Geometry} by first transforming it into a projected coordinate reference system and then back
     * to WGS 84.
     *
     * @param geometry         The {@link Geometry}.
     * @param meters           The buffer width.
     * @param quadrantSegments The number of line segments used to represent a quadrant of a circle.
     * @param endCapStyle      The end cap style to use.
     * @return The buffered {@link Geometry}.
     * @throws GeometryException If the {@link Geometry} is not using WGS84.
     * @see Geometry#buffer(double, int, int)
     * @see BufferParameters#CAP_FLAT
     * @see BufferParameters#CAP_ROUND
     * @see BufferParameters#CAP_SQUARE
     */
    public static Geometry buffer(Geometry geometry, double meters, int quadrantSegments, int endCapStyle)
            throws GeometryException {
        if (geometry.getSRID() != 0 && geometry.getSRID() != 4326) {
            throw new GeometryException("unsupported coordinate reference system: " + geometry.getSRID());
        }
        try {
            CoordinateReferenceSystem auto = getProjectedCRS(geometry);
            MathTransform toTransform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, auto);
            MathTransform fromTransform = CRS.findMathTransform(auto, DefaultGeographicCRS.WGS84);
            Geometry projectedGeometry = JTS.transform(geometry, toTransform);
            Geometry projectedBufferedGeometry = projectedGeometry.buffer(meters, quadrantSegments, endCapStyle);
            return JTS.transform(projectedBufferedGeometry, fromTransform);
        } catch (FactoryException | TransformException e) {
            throw new GeometryException(e);
        }
    }

    private static CoordinateReferenceSystem getProjectedCRS(Geometry geometry) throws FactoryException {
        Coordinate centroid = geometry.getCentroid().getCoordinate();
        return CRS.decode(String.format("AUTO:42001,%s,%s", centroid.getX(), centroid.getY()));
    }

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JtsModule());

    public static String toString(Geometry geometry) {
        try {
            Feature feature = new Feature();
            feature.setGeometry(geometry);
            feature.setProperties(mapper.createObjectNode());
            return mapper.writeValueAsString(feature);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
