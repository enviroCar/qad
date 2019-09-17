package org.envirocar.qad.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.envirocar.qad.AxisModelParser;
import org.envirocar.qad.AxisModelRepository;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.axis.AxisModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class AxisModelConfiguration {

    private static final String KREFELD = "krefeld";
    private static final String HAMM = "hamm";
    private static final String CHEMNITZ = "chemnitz";

    @Bean
    public AxisModelRepository axisModelRepository(List<AxisModel> axisModels) {
        return new AxisModelRepository(axisModels);
    }

    @Bean(name = KREFELD)
    public AxisModel modelKrefeld(ObjectMapper mapper, AxisModelParser parser) throws IOException {
        return readModel(mapper, parser, "/Krefeld.json");
    }

    @Bean(name = HAMM)
    public AxisModel modelHamm(ObjectMapper mapper, AxisModelParser parser) throws IOException {
        return readModel(mapper, parser, "/Hamm.json");
    }

    @Bean(name = CHEMNITZ)
    public AxisModel modelChemnitz(ObjectMapper mapper, AxisModelParser parser) throws IOException {
        return readModel(mapper, parser, "/Chemnitz.json");
    }

    private AxisModel readModel(ObjectMapper mapper, AxisModelParser parser, String path) throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            return parser.createAxisModel(mapper.readValue(stream, FeatureCollection.class));
        }
    }
}
