package org.envirocar.qad.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.envirocar.qad.axis.AxisModel;
import org.envirocar.qad.axis.AxisModelParser;
import org.envirocar.qad.model.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Configuration
public class AxisModelConfiguration {

    private static final String KREFELD = "KRE";
    private static final String HAMM = "HAM";
    private static final String CHEMNITZ = "CHE";
    private final AxisModelParser parser;
    private final ObjectMapper mapper;

    @Autowired
    public AxisModelConfiguration(AxisModelParser parser, ObjectMapper mapper) {
        this.parser = Objects.requireNonNull(parser);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Bean(name = KREFELD)
    public AxisModel kre() throws IOException {
        return readModel("/model/Krefeld-2019-10-23.json");
    }

    @Bean(name = HAMM)
    public AxisModel ham() throws IOException {
        return readModel("/model/Hamm-2019-10-23.json");
    }

    @Bean(name = CHEMNITZ)
    public AxisModel che() throws IOException {
        return readModel("/model/Chemnitz-2020-01-28.json");
    }

    private AxisModel readModel(String path) throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            FeatureCollection collection = this.mapper.readValue(stream, FeatureCollection.class);
            return this.parser.createAxisModel(collection);
        }
    }
}
