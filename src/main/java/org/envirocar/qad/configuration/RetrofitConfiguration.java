package org.envirocar.qad.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.envirocar.qad.mapmatching.MapMatchingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.URL;

@Configuration
public class RetrofitConfiguration {

    @Bean
    public Retrofit retrofit(JacksonConverterFactory factory, OkHttpClient client) {
        return new Retrofit.Builder().addConverterFactory(factory).client(client).build();
    }

    @Bean
    public JacksonConverterFactory jacksonConverterFactory(ObjectMapper mapper) {
        return JacksonConverterFactory.create(mapper);
    }

    @Bean
    public MapMatchingService mapMatchingService(Retrofit retrofit, @Value("{qad.mapMatching.url}") URL url) {
        return retrofit.newBuilder().baseUrl(url).build().create(MapMatchingService.class);
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                       .followRedirects(true)
                       .followSslRedirects(true)
                       .addInterceptor(new LoggingInterceptor())
                       .build();
    }

}
