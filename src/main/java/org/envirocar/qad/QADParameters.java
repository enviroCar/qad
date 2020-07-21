package org.envirocar.qad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;
import java.nio.file.Path;

@ConfigurationProperties(prefix = "qad")
public class QADParameters {

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
    private boolean archive = true;
    private boolean simplifyLengthCalculation;
    private URL enviroCarApiURL;

    public URL getEnviroCarApiURL() {
        return this.enviroCarApiURL;
    }

    public void setEnviroCarApiURL(URL enviroCarApiURL) {
        this.enviroCarApiURL = enviroCarApiURL;
    }

    public boolean isArchive() {
        return this.archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public double getLengthDifferenceToTolerate() {
        return this.lengthDifferenceToTolerate;
    }

    public void setLengthDifferenceToTolerate(double lengthDifferenceToTolerate) {
        this.lengthDifferenceToTolerate = lengthDifferenceToTolerate;
    }

    public double getSnappingTolerance() {
        return this.snappingTolerance;
    }

    public void setSnappingTolerance(double snappingTolerance) {
        this.snappingTolerance = snappingTolerance;
    }

    public boolean isSimplifyLengthCalculation() {
        return this.simplifyLengthCalculation;
    }

    public void setSimplifyLengthCalculation(boolean simplifyLengthCalculation) {
        this.simplifyLengthCalculation = simplifyLengthCalculation;
    }

    public Path getOutputPath() {
        return this.outputPath;
    }

    public void setOutputPath(Path outputPath) {
        this.outputPath = outputPath;
    }

    public MapMatching getMapMatching() {
        return this.mapMatching;
    }

    public void setMapMatching(MapMatching mapMatching) {
        this.mapMatching = mapMatching;
    }

    public double getMaxAngleDeviation() {
        return this.maxAngleDeviation;
    }

    public void setMaxAngleDeviation(double maxAngleDeviation) {
        this.maxAngleDeviation = maxAngleDeviation;
    }

    public double getMaxLengthDeviation() {
        return this.maxLengthDeviation;
    }

    public void setMaxLengthDeviation(double maxLengthDeviation) {
        this.maxLengthDeviation = maxLengthDeviation;
    }

    public Densify getDensify() {
        return this.densify;
    }

    public void setDensify(Densify densify) {
        this.densify = densify;
    }

    public Stops getStops() {
        return this.stops;
    }

    public void setStops(Stops stops) {
        this.stops = stops;
    }

    public Segments getSegments() {
        return this.segments;
    }

    public UTurn getUTurn() {
        return this.uturn;
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
            return this.url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }
    }

    public static class Stops {
        private double startThresholdSpeed = 5.0d;
        private double endThresholdSpeed = 10.0d;

        public double getStartThresholdSpeed() {
            return this.startThresholdSpeed;
        }

        public void setStartThresholdSpeed(double startThresholdSpeed) {
            this.startThresholdSpeed = startThresholdSpeed;
        }

        public double getEndThresholdSpeed() {
            return this.endThresholdSpeed;
        }

        public void setEndThresholdSpeed(double endThresholdSpeed) {
            this.endThresholdSpeed = endThresholdSpeed;
        }
    }

    public static class Segments {
        private double bufferSize = 10;

        public double getBufferSize() {
            return this.bufferSize;
        }

        public void setBufferSize(double bufferSize) {
            this.bufferSize = bufferSize;
        }
    }

    public static class Densify {
        private int numPoints = 4;

        public int getNumPoints() {
            return this.numPoints;
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
            return this.windowSize;
        }

        public void setWindowSize(int windowSize) {
            this.windowSize = windowSize;
        }

        public double getBufferSize() {
            return this.bufferSize;
        }

        public void setBufferSize(double bufferSize) {
            this.bufferSize = bufferSize;
        }

        public double getMinAngleDeviation() {
            return this.minAngleDeviation;
        }

        public void setMinAngleDeviation(double minAngleDeviation) {
            this.minAngleDeviation = minAngleDeviation;
        }
    }
}
