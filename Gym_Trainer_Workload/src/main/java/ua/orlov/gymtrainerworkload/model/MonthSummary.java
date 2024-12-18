package ua.orlov.gymtrainerworkload.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.orlov.gymtrainerworkload.mapper.MonthDeserializer;
import ua.orlov.gymtrainerworkload.mapper.MonthSerializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthSummary {

    @JsonSerialize(using = MonthSerializer.class)
    @JsonDeserialize(using = MonthDeserializer.class)
    private Month month;

    private int durationMinutes;
}
