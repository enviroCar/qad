package org.envirocar.qad.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.GeometryFactory;
import org.n52.jackson.datatype.jts.IncludeBoundingBox;
import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {
    @Bean
    public ObjectMapper objectMapper(JtsModule jtsModule) {
        return new ObjectMapper()
                       .findAndRegisterModules()
                       .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                       .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                       .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                       .registerModule(jtsModule);
    }

    @Bean
    public JtsModule jtsModule(GeometryFactory geometryFactory) {
        return new JtsModule(geometryFactory, IncludeBoundingBox.never());
    }
}
