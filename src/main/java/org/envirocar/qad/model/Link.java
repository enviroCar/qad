package org.envirocar.qad.model;

import org.envirocar.qad.EnviroCarApi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

public class Link {
    private final String url;
    private final Map<String, String> param;

    public Link(String url, Map<String, String> param) {
        this.url = Objects.requireNonNull(url);
        this.param = Optional.ofNullable(param).orElseGet(HashMap::new);
    }

    public String getRel() {
        return this.param.get("rel");
    }

    public void setRel(String rel) {
        this.param.put("rel", rel);
    }

    public String getType() {
        return this.param.get("type");
    }

    public void setType(String type) {
        this.param.put("type", type);
    }

    public String getUrl() {
        return this.url;
    }

    public static Link parse(String header) throws IllegalArgumentException {
        header = header.trim();
        if (header.charAt(0) != '<') {
            throw new IllegalArgumentException();
        }
        int urlEnd = header.indexOf('>');
        if (urlEnd < 0 || urlEnd == header.length()) {
            throw new IllegalArgumentException();
        }
        String url = header.substring(1, urlEnd);
        if (header.charAt(urlEnd + 1) != ';') {
            throw new IllegalArgumentException();
        }
        Map<String, String> param = null;
        int paramStart = header.indexOf(';', urlEnd + 1);

        if (paramStart > 0) {
            param = Arrays.stream(header.substring(paramStart + 1).split(";"))
                          .map(x -> x.split("=", 2))
                          .collect(toMap(x -> x[0], x -> x[1]));
        }

        return new Link(url, param);
    }
}
