package org.envirocar.qad.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.envirocar.qad.axis.AxisModel;
import org.envirocar.qad.axis.AxisModelParser;
import org.envirocar.qad.axis.AxisModelRepository;
import org.envirocar.qad.model.FeatureCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class AxisModelConfiguration {

    private static final String KREFELD = "KRE";
    private static final String HAMM = "HAM";
    private static final String CHEMNITZ = "CHE";

    @Bean
    public AxisModelRepository axisModelRepository(List<AxisModel> axisModels) {
        return new AxisModelRepository(axisModels);
    }

    @Bean(name = KREFELD)
    public AxisModel modelKrefeld(ObjectMapper mapper, AxisModelParser parser) throws IOException {
        return readModel(mapper, parser, "/Krefeld-2019-10-23.json");
    }

    @Bean(name = HAMM)
    public AxisModel modelHamm(ObjectMapper mapper, AxisModelParser parser) throws IOException {
        return readModel(mapper, parser, "/Hamm-2019-10-23.json");
    }

    @Bean(name = CHEMNITZ)
    public AxisModel modelChemnitz(ObjectMapper mapper, AxisModelParser parser) throws IOException {
        return readModel(mapper, parser, "/Chemnitz-2019-11-18.json");
    }

    private AxisModel readModel(ObjectMapper mapper, AxisModelParser parser, String path) throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            return parser.createAxisModel(mapper.readValue(stream, FeatureCollection.class));
        }
    }
}
