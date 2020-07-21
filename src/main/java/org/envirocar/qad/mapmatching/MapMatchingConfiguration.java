package org.envirocar.qad.mapmatching;

import org.envirocar.qad.QADParameters;
import org.envirocar.qad.configuration.RetrofitConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@ConditionalOnProperty(name = "qad.mapMatching.enabled", matchIfMissing = true)
public class MapMatchingConfiguration {
    private final QADParameters parameters;
    private final RetrofitConfiguration retrofit;

    @Autowired
    public MapMatchingConfiguration(QADParameters parameters,
                                    RetrofitConfiguration retrofit) {
        this.parameters = Objects.requireNonNull(parameters);
        this.retrofit = Objects.requireNonNull(retrofit);
    }

    @Bean
    public MapMatchingService mapMatchingService() {
        return this.retrofit.builder().baseUrl(this.parameters.getMapMatching().getUrl())
                            .build().create(MapMatchingService.class);
    }

}
