package org.envirocar.qad.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.envirocar.qad.AlgorithmParameters;
import org.envirocar.qad.mapmatching.MapMatcher;
import org.envirocar.qad.mapmatching.MapMatcherImpl;
import org.envirocar.qad.mapmatching.MapMatchingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
