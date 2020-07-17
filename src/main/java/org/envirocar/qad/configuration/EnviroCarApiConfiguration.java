package org.envirocar.qad.configuration;

import okhttp3.OkHttpClient;
import org.envirocar.qad.EnviroCarApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class EnviroCarApiConfiguration {
    private Retrofit.Builder retrofit(JacksonConverterFactory factory, OkHttpClient client) {
        return new Retrofit.Builder().addConverterFactory(factory).client(client);
    }

    @Bean
    public EnviroCarApi enviroCarApi(JacksonConverterFactory factory, OkHttpClient client) {
        return retrofit(factory, client).baseUrl("https://envirocar.org/api/stable/")
                                        .build().create(EnviroCarApi.class);
    }

}
