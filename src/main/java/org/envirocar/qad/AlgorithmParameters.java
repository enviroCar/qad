package org.envirocar.qad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;
import java.nio.file.Path;

@ConfigurationProperties(prefix = "qad")
public class AlgorithmParameters {

    private double maxAngleDeviation = 180.0d;
    private double maxLengthDeviation = 0.20d;
    private double snappingTolerance = 20.0d;
    private double lengthDifferenceToTolerate = 10.0d;
    private Densify densify = new Densify();
    private Stops stops = new Stops();
    private Segments segments = new Segments();
    private MapMatching mapMatching = new MapMatching();
    private UTurn uturn = new UTurn();
    private Path outputPath;
    private boolean simplifyLengthCalculation = false;

    public double getLengthDifferenceToTolerate() {
        return lengthDifferenceToTolerate;
    }

    public void setLengthDifferenceToTolerate(double lengthDifferenceToTolerate) {
        this.lengthDifferenceToTolerate = lengthDifferenceToTolerate;
    }

    public double getSnappingTolerance() {
        return snappingTolerance;
    }

    public void setSnappingTolerance(double snappingTolerance) {
        this.snappingTolerance = snappingTolerance;
    }

    public boolean isSimplifyLengthCalculation() {
        return simplifyLengthCalculation;
    }

    public void setSimplifyLengthCalculation(boolean simplifyLengthCalculation) {
        this.simplifyLengthCalculation = simplifyLengthCalculation;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(Path outputPath) {
        this.outputPath = outputPath;
    }

    public MapMatching getMapMatching() {
        return mapMatching;
    }

    public void setMapMatching(MapMatching mapMatching) {
        this.mapMatching = mapMatching;
    }

    public double getMaxAngleDeviation() {
        return maxAngleDeviation;
    }

    public void setMaxAngleDeviation(double maxAngleDeviation) {
        this.maxAngleDeviation = maxAngleDeviation;
    }

    public double getMaxLengthDeviation() {
        return maxLengthDeviation;
    }

    public void setMaxLengthDeviation(double maxLengthDeviation) {
        this.maxLengthDeviation = maxLengthDeviation;
    }

    public Densify getDensify() {
        return densify;
    }

    public void setDensify(Densify densify) {
        this.densify = densify;
    }

    public Stops getStops() {
        return stops;
    }

    public void setStops(Stops stops) {
        this.stops = stops;
    }

    public Segments getSegments() {
        return segments;
    }

    public UTurn getUTurn() {
        return uturn;
    }

    public void setUTurn(UTurn uturn) {
        this.uturn = uturn;
    }

    public void setSegments(Segments segments) {
        this.segments = segments;
    }

    public static class MapMatching {
        @Value("https://processing.envirocar.org/mapmatching/")
        private URL url;

        public URL getUrl() {
            return url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }
    }

    public static class Stops {
        private double startThresholdSpeed = 5.0d;
        private double endThresholdSpeed = 10.0d;

        public double getStartThresholdSpeed() {
            return startThresholdSpeed;
        }

        public void setStartThresholdSpeed(double startThresholdSpeed) {
            this.startThresholdSpeed = startThresholdSpeed;
        }

        public double getEndThresholdSpeed() {
            return endThresholdSpeed;
        }

        public void setEndThresholdSpeed(double endThresholdSpeed) {
            this.endThresholdSpeed = endThresholdSpeed;
        }
    }

    public static class Segments {
        private double bufferSize = 10;

        public double getBufferSize() {
            return bufferSize;
        }

        public void setBufferSize(double bufferSize) {
            this.bufferSize = bufferSize;
        }
    }

    public static class Densify {
        private int numPoints = 4;

        public int getNumPoints() {
            return numPoints;
        }

        public void setNumPoints(int numPoints) {
            this.numPoints = numPoints;
        }
    }

    public static class UTurn {
        private int windowSize = 45;
        private double bufferSize = 20.0d;
        private double minAngleDeviation = 170.0d;

        public int getWindowSize() {
            return windowSize;
        }

        public void setWindowSize(int windowSize) {
            this.windowSize = windowSize;
        }

        public double getBufferSize() {
            return bufferSize;
        }

        public void setBufferSize(double bufferSize) {
            this.bufferSize = bufferSize;
        }

        public double getMinAngleDeviation() {
            return minAngleDeviation;
        }

        public void setMinAngleDeviation(double minAngleDeviation) {
            this.minAngleDeviation = minAngleDeviation;
        }
    }
}
