package ua.orlov.springcoregym.mapper.traineetrainer;

import org.mapstruct.InjectionStrategy;
import ua.orlov.springcoregym.dto.trainee.TraineeFullResponse;
import ua.orlov.springcoregym.dto.trainee.TraineeFullUsernameResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerFullResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerFullUsernameResponse;
import ua.orlov.springcoregym.mapper.trainee.TraineeMapper;
import ua.orlov.springcoregym.mapper.trainer.TrainerMapper;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {TrainerMapper.class, TraineeMapper.class, TrainingTypeMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TraineeTrainerMapper {

    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "trainers", target = "trainers", qualifiedByName = "trainersListToTrainerResponseList")
    TraineeFullResponse traineeToTraineeFullResponse(Trainee trainee);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "trainers", target = "trainers", qualifiedByName = "trainersListToTrainerResponseList")
    TraineeFullUsernameResponse traineeToTraineeFullUsernameResponse(Trainee trainee);

    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "specialization", target = "specialization", qualifiedByName = "trainingTypeToTrainingTypeResponse")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "trainees", target = "trainees", qualifiedByName = "traineeListToTraineeNamesResponseList")
    TrainerFullResponse trainerToTrainerFullResponse(Trainer trainer);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "specialization", target = "specialization", qualifiedByName = "trainingTypeToTrainingTypeResponse")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "trainees", target = "trainees", qualifiedByName = "traineeListToTraineeNamesResponseList")
    TrainerFullUsernameResponse trainerToTrainerFullUsernameResponse(Trainer trainer);
}

