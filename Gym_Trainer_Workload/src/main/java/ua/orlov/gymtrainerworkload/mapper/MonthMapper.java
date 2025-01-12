package ua.orlov.gymtrainerworkload.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.model.MonthSummary;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MonthMapper {

    @Mapping(target = "month", expression = "java(ua.orlov.gymtrainerworkload.model.Month.fromOrder(trainerWorkload.getTrainingDate().getMonthValue()))")
    @Mapping(target = "durationMinutes", source = "trainingDurationMinutes")
    MonthSummary trainerWorkloadToMonthSummary(TrainerWorkload trainerWorkload);

    default List<MonthSummary> mapToMonthSummaries(TrainerWorkload trainerWorkload) {
        return List.of(trainerWorkloadToMonthSummary(trainerWorkload));
    }
}
