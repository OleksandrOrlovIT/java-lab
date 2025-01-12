package ua.orlov.gymtrainerworkload.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.model.YearSummary;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MonthMapper.class})
public interface YearMapper {

    @Mapping(target = "year", source = "trainingDate.year")
    @Mapping(target = "months", source = "trainerWorkload")
    YearSummary trainerWorkloadToYearSummary(TrainerWorkload trainerWorkload);

    default List<YearSummary> mapToYearSummaries(TrainerWorkload trainerWorkload) {
        return List.of(trainerWorkloadToYearSummary(trainerWorkload));
    }
}
