package org.envirocar.qad;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.n52.jackson.datatype.jts.IncludeBoundingBox;
import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                       .findAndRegisterModules()
                       .registerModule(new JtsModule(IncludeBoundingBox.never()));
    }

}
