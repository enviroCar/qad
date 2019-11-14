package org.envirocar.qad.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Objects;

public class CustomDoubleSerializer extends JsonSerializer<Double> {
    private final NumberFormat format;

    public CustomDoubleSerializer(NumberFormat format) {
        this.format = Objects.requireNonNull(format);
    }

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (null == value) {
            gen.writeNull();
        } else {
            gen.writeNumber(format.format(value));
        }
    }

}
