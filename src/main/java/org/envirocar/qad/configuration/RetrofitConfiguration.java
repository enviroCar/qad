package org.envirocar.qad.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Configuration
public class RetrofitConfiguration {
    private final ObjectMapper mapper;

    @Autowired
    public RetrofitConfiguration(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Bean
    public JacksonConverterFactory jacksonConverterFactory() {
        return JacksonConverterFactory.create(this.mapper);
    }

    @Bean("retrofitBuilder")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Retrofit.Builder builder() {
        return new Retrofit.Builder().addConverterFactory(jacksonConverterFactory()).client(okHttpClient());
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                       .followRedirects(true)
                       .followSslRedirects(true)
                       .connectTimeout(10, TimeUnit.SECONDS)
                       .writeTimeout(10, TimeUnit.SECONDS)
                       .readTimeout(5, TimeUnit.MINUTES)
                       .addInterceptor(new LoggingInterceptor())
                       .build();
    }

}
