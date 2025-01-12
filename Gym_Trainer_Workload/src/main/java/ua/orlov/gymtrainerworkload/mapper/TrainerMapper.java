package ua.orlov.gymtrainerworkload.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.model.Trainer;

@Mapper(componentModel = "spring", uses = {YearMapper.class})
public interface TrainerMapper {

    @Mapping(target = "username", source = "trainerUsername")
    @Mapping(target = "firstName", source = "trainerFirstName")
    @Mapping(target = "lastName", source = "trainerLastName")
    @Mapping(target = "status", source = "trainerActive")
    @Mapping(target = "years", source = "trainerWorkload")
    Trainer trainerWorkloadToTrainer(TrainerWorkload trainerWorkload);
}
