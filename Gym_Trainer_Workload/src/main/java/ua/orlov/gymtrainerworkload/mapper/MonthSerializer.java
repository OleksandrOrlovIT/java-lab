package ua.orlov.gymtrainerworkload.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ua.orlov.gymtrainerworkload.model.Month;

import java.io.IOException;

public class MonthSerializer extends StdSerializer<Month> {

    public MonthSerializer() {
        super(Month.class);
    }

    @Override
    public void serialize(Month month, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(month.getOrder());
    }
}
