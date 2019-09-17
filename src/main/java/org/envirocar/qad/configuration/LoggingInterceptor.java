package org.envirocar.qad.configuration;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class LoggingInterceptor implements Interceptor {
    private final Logger log;

    public LoggingInterceptor(Logger log) {
        this.log = Objects.requireNonNull(log);
    }

    public LoggingInterceptor() {
        this(LoggerFactory.getLogger("okhttp3"));
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Instant before = Instant.now();
        Request request = chain.request();
        Response response = chain.proceed(request);
        Instant after = Instant.now();

        if (response.isSuccessful()) {
            log.debug("{} {}: {} {}",
                      request.method(),
                      request.url(),
                      response.code(),
                      Duration.between(before, after));
        } else {
            log.warn("{} {}: {} {}",
                     request.method(),
                     request.url(),
                     response.code(),
                     Duration.between(before, after));
        }
        return response;
    }
}
