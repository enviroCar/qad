package org.envirocar.qad.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.envirocar.qad.ObjectReaderTest;
import org.envirocar.qad.QADParameters;
import org.envirocar.qad.axis.AxisId;
import org.envirocar.qad.axis.ModelId;
import org.envirocar.qad.model.result.AnalysisResult;
import org.envirocar.qad.model.result.SegmentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DirectoryResultPersistenceTest {
    @TempDir
    public Path temp;
    private ObjectReader reader;
    private DirectoryResultPersistence persistence;

    @BeforeEach
    public void setup() {
        QADParameters QADParameters = new QADParameters();
        QADParameters.setOutputPath(this.temp);

        ObjectMapper objectMapper = new ObjectMapper()
                                            .setLocale(Locale.ROOT)
                                            .findAndRegisterModules()
                                            .registerModule(new Jdk8Module())
                                            .registerModule(new JavaTimeModule());
        this.reader = objectMapper.reader();
        this.persistence = new DirectoryResultPersistence(this.reader, objectMapper.writer(), QADParameters);
    }

    @Test
    public void testDeduplication() throws IOException {
        AnalysisResult result = read();
        this.persistence.persist1(result);

        assertThat(Files.list(this.temp).count(), is(1L));
        assertFalse(this.persistence.persist1(result));
        assertThat(Files.list(this.temp).count(), is(1L));

        // change of track id
        String track = result.getTrack();
        result.setTrack("5efef1ed604fbd6206b36d68");
        assertFalse(this.persistence.persist1(result));
        result.setTrack(track);
        assertThat(Files.list(this.temp).count(), is(1L));

        // change of start time
        Instant start = result.getStart();
        result.setStart(Instant.now());
        assertTrue(this.persistence.persist1(result));
        result.setStart(start);
        assertThat(Files.list(this.temp).count(), is(2L));

        // change of end time
        Instant end = result.getEnd();
        result.setEnd(Instant.now());
        assertTrue(this.persistence.persist1(result));
        result.setEnd(end);
        assertThat(Files.list(this.temp).count(), is(3L));

        // change of axis
        AxisId axis = result.getAxis();
        result.setAxis(new AxisId(1, 1));
        assertTrue(this.persistence.persist1(result));
        result.setAxis(axis);
        assertThat(Files.list(this.temp).count(), is(4L));

        // change of model
        ModelId model = result.getModel();
        result.setModel(new ModelId("HAM", "2020-02-02"));
        assertTrue(this.persistence.persist1(result));
        result.setModel(model);
        assertThat(Files.list(this.temp).count(), is(5L));

        // change of fuel type
        String fuelType = result.getFuelType();
        result.setFuelType("diesel");
        assertTrue(this.persistence.persist1(result));
        result.setFuelType(fuelType);
        assertThat(Files.list(this.temp).count(), is(6L));

        // different segment
        int segmentId = result.getSegments().get(0).getSegmentId();
        result.getSegments().get(0).setSegmentId(10);
        assertTrue(this.persistence.persist1(result));
        result.getSegments().get(0).setSegmentId(segmentId);
        assertThat(Files.list(this.temp).count(), is(7L));

        // additional segment
        SegmentResult sr = new SegmentResult();
        sr.setSegmentId(20);
        result.getSegments().add(sr);
        assertTrue(this.persistence.persist1(result));
        result.setFuelType(fuelType);
        assertThat(Files.list(this.temp).count(), is(8L));

    }

    private AnalysisResult read() throws IOException {
        try (InputStream stream = ObjectReaderTest.class
                                          .getResourceAsStream("/HAM_404_3_100733_20200703_5efef1ed604fbd6206b36d67.json")) {
            return this.reader.readValue(stream, AnalysisResult.class);
        }
    }
}