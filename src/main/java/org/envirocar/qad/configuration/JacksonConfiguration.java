package org.envirocar.qad.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.locationtech.jts.geom.GeometryFactory;
import org.n52.jackson.datatype.jts.IncludeBoundingBox;
import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectWriter objectWriter(ObjectMapper mapper) {
        return mapper.writer();
    }

    @Bean
    public ObjectReader objectReader(ObjectMapper mapper) {
        return mapper.reader();
    }

    @Bean
    public JsonNodeFactory jsonNodeFactory() {
        return JsonNodeFactory.withExactBigDecimals(true);
    }

    @Bean
    public ObjectMapper objectMapper(JtsModule jtsModule) {
        return new ObjectMapper().findAndRegisterModules()
                                 .registerModule(new Jdk8Module())
                                 .registerModule(new JavaTimeModule())
                                 .registerModule(jtsModule)
                                 .enable(SerializationFeature.INDENT_OUTPUT)
                                 .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
                                 .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                                 .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                                 .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                                 .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                                 .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                                 .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                                 .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                                 .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

    @Bean
    public JtsModule jtsModule(GeometryFactory geometryFactory) {
        return new JtsModule(geometryFactory, IncludeBoundingBox.never());
    }
}
