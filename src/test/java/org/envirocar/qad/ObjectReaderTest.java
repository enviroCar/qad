package org.envirocar.qad;

import com.fasterxml.jackson.databind.ObjectReader;
import org.envirocar.qad.axis.AxisId;
import org.envirocar.qad.axis.ModelId;
import org.envirocar.qad.model.result.AnalysisResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ObjectReaderTest {
    @Autowired
    private ObjectReader reader;
    @Rule
    public final ErrorCollector errors = new ErrorCollector();
    private final DateTimeFormatter format = DateTimeFormatter
                                                     .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXX", Locale.ROOT);

    @Test
    public void testReadingAnalysisResult() throws IOException {
        try (InputStream stream = ObjectReaderTest.class
                                          .getResourceAsStream("/HAM_404_3_100733_20200703_5efef1ed604fbd6206b36d67.json")) {
            AnalysisResult analysisResult = this.reader.readValue(stream, AnalysisResult.class);
            errors.checkThat(analysisResult, is(not(nullValue())));
            errors.checkThat(analysisResult.getAxis(), is(equalTo(new AxisId(404, 3))));
            errors.checkThat(analysisResult.getTrack(), is(equalTo("5efef1ed604fbd6206b36d67")));
            errors.checkThat(analysisResult.getModel(), is(equalTo(new ModelId("HAM", "2019-10-23"))));

                             errors.checkThat(analysisResult
                                                      .getStart(), is(equalTo(Instant.from(this.format
                                                                                                   .parse("2020-07-03T10:07:33.000+0200")))));
            errors.checkThat(analysisResult
                                     .getEnd(), is(equalTo(Instant.from(this.format
                                                                                .parse("2020-07-03T10:07:58.800+0200")))));
            errors.checkThat(analysisResult.getFuelType(), is(equalTo("electric")));

            errors.checkThat(analysisResult.getSegments().size(), is(1));
            errors.checkThat(analysisResult.getSegments().get(0).getSegmentId(), is(0));
        }
        ;
    }
}
