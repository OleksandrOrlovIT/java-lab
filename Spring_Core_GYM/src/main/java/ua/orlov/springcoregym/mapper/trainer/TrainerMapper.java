package ua.orlov.springcoregym.mapper.trainer;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ua.orlov.springcoregym.dto.trainer.TrainerRegister;
import ua.orlov.springcoregym.dto.trainer.TrainerResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerWorkload;
import ua.orlov.springcoregym.dto.trainer.UpdateTrainerRequest;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.ActionType;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.training.TrainingType;
import ua.orlov.springcoregym.model.user.Trainer;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {TrainingTypeMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TrainerMapper {

    @Mapping(target = "firstName", source = "trainerRegister.firstName")
    @Mapping(target = "lastName", source = "trainerRegister.lastName")
    Trainer trainerRegisterToTrainer(TrainerRegister trainerRegister);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    UsernamePasswordUser trainerToUsernamePasswordUser(Trainer trainer);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "specialization", source = "specialization", qualifiedByName = "trainingTypeToTrainingTypeResponse")
    TrainerResponse trainerToTrainerResponse(Trainer trainer);

    @Named("trainersListToTrainerResponseList")
    List<TrainerResponse> trainersListToTrainerResponseList(List<Trainer> trainers);

    @Mapping(target = "username", source = "request.username")
    @Mapping(target = "firstName", source = "request.firstName")
    @Mapping(target = "lastName", source = "request.lastName")
    @Mapping(target = "specialization", source = "trainingType")
    @Mapping(target = "active", source = "request.active")
    Trainer updateTrainerRequestToTrainer(UpdateTrainerRequest request, TrainingType trainingType);

    @Mapping(target = "trainerUsername", source = "trainer.username")
    @Mapping(target = "trainerFirstName", source = "trainer.firstName")
    @Mapping(target = "trainerLastName", source = "trainer.lastName")
    @Mapping(target = "trainerActive", source = "trainer.active")
    @Mapping(target = "trainingDate", source = "training.trainingDate")
    @Mapping(target = "trainingDurationMinutes", source = "training.trainingDurationMinutes")
    @Mapping(target = "actionType", source = "actionType")
    TrainerWorkload trainerToTrainerWorkload(Trainer trainer, Training training, ActionType actionType);
}
