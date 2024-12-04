package ua.orlov.gymtrainerworkload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearSummary {

    private int year;

    private List<MonthSummary> months;

}
