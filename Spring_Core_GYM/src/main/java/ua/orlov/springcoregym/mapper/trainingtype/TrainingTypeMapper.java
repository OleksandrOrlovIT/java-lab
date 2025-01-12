package ua.orlov.springcoregym.mapper.trainingtype;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;
import ua.orlov.springcoregym.model.training.TrainingType;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    @Named("trainingTypeToTrainingTypeResponse")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "trainingTypeName", source = "trainingTypeName")
    TrainingTypeResponse trainingTypeToTrainingTypeResponse(TrainingType trainingType);

    List<TrainingTypeResponse> trainingTypeListToTrainingTypeResponseList(List<TrainingType> trainingTypes);
}
