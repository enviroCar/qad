package org.envirocar.qad.configuration;

import org.envirocar.qad.EnviroCarApi;
import org.envirocar.qad.QADParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class EnviroCarApiConfiguration {

    private final RetrofitConfiguration retrofit;
    private final QADParameters qadParameters;

    @Autowired
    public EnviroCarApiConfiguration(RetrofitConfiguration retrofit,
                                     QADParameters qadParameters) {
        this.retrofit = Objects.requireNonNull(retrofit);
        this.qadParameters = Objects.requireNonNull(qadParameters);
    }

    @Bean
    public EnviroCarApi enviroCarApi() {
        return this.retrofit.builder()
                            .baseUrl(this.qadParameters.getEnviroCarApiURL())
                            .build()
                            .create(EnviroCarApi.class);
    }

}
