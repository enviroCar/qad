package org.envirocar.qad.configuration;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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

    @Nonnull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Instant before = Instant.now();
        Request request = chain.request();
        Response response = chain.proceed(request);
        Instant after = Instant.now();

        if (response.isSuccessful()) {
            this.log.debug("{} {}: {} {}",
                           request.method(),
                           request.url(),
                           response.code(),
                           Duration.between(before, after));
        } else {
            this.log.warn("{} {}: {} {}",
                          request.method(),
                          request.url(),
                          response.code(),
                          Duration.between(before, after));
        }
        return response;
    }
}
