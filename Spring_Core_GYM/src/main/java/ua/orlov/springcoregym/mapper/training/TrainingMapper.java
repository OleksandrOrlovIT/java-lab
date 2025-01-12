package ua.orlov.springcoregym.mapper.training;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.orlov.springcoregym.dto.training.CreateTrainingRequest;
import ua.orlov.springcoregym.dto.training.TrainingFullResponse;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.training.Training;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {TrainingTypeMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TrainingMapper {

    @Mapping(source = "id", target = "trainingId")
    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingType", target = "trainingType", qualifiedByName = "trainingTypeToTrainingTypeResponse")
    @Mapping(source = "trainingDurationMinutes", target = "trainingDurationMinutes")
    @Mapping(source = "trainer.username", target = "trainerName")
    TrainingFullResponse trainingToTrainingFullResponse(Training training);

    List<TrainingFullResponse> trainingListToTrainingFullResponseList(List<Training> trainings);

    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingDurationMinutes", target = "trainingDurationMinutes")
    Training createTrainingRequestToTraining(CreateTrainingRequest request);
}
