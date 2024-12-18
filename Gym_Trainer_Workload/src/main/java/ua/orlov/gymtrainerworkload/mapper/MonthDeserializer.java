package ua.orlov.gymtrainerworkload.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ua.orlov.gymtrainerworkload.model.Month;

import java.io.IOException;

public class MonthDeserializer extends StdDeserializer<Month> {

    public MonthDeserializer() {
        super(Month.class);
    }

    @Override
    public Month deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int order = p.getIntValue();
        return Month.fromOrder(order);
    }
}
