package org.envirocar.qad.mapmatching;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.envirocar.qad.AlgorithmParameters;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
@ConditionalOnProperty(name = "qad.mapMatching.enabled", matchIfMissing = true)
public class MapMatchingConfiguration {
    private Retrofit.Builder retrofit(JacksonConverterFactory factory, OkHttpClient client) {
        return new Retrofit.Builder().addConverterFactory(factory).client(client);
    }

    @Bean
    public JacksonConverterFactory jacksonConverterFactory(ObjectMapper mapper) {
        return JacksonConverterFactory.create(mapper);
    }

    @Bean
    public MapMatcher mapMatcher(MapMatchingService service) {
        return new MapMatcherImpl(service);
    }

    @Bean
    public MapMatchingService mapMatchingService(JacksonConverterFactory factory, OkHttpClient client,
                                                 AlgorithmParameters parameters) {
        return retrofit(factory, client).baseUrl(parameters.getMapMatching().getUrl())
                                        .build().create(MapMatchingService.class);
    }

}
