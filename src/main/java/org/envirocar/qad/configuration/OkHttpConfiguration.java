package org.envirocar.qad.configuration;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkHttpConfiguration {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                       .followRedirects(true)
                       .followSslRedirects(true)
                       .addInterceptor(new LoggingInterceptor())
                       .build();
    }

}
