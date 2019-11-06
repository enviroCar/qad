package org.envirocar.qad.configuration;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JtsConfiguration {
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

    @Bean
    public static GeometryFactory geometryFactory() {
        return GEOMETRY_FACTORY;
    }
}
