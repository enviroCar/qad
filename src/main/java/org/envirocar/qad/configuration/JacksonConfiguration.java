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

import java.util.Locale;

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
    public ObjectMapper objectMapper(Jdk8Module jdk8Module, JavaTimeModule javaTimeModule, JtsModule jtsModule) {
        return new ObjectMapper().setLocale(Locale.ROOT)
                                 .findAndRegisterModules()
                                 .registerModule(jdk8Module)
                                 .registerModule(javaTimeModule)
                                 .registerModule(jtsModule)
                                 .configure(SerializationFeature.INDENT_OUTPUT, true)
                                 .configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false)
                                 .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                                 .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
                                 .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false)
                                 .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                                 .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
                                 .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
                                 .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                                 .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
                                 .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
    }

    @Bean
    public Jdk8Module jdk8Module() {
        return new Jdk8Module();
    }

    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean
    public JtsModule jtsModule(GeometryFactory geometryFactory) {
        return new JtsModule(geometryFactory, IncludeBoundingBox.never(), 8);
    }
}
