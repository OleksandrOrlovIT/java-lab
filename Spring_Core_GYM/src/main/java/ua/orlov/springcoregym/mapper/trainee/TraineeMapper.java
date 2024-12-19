package ua.orlov.springcoregym.mapper.trainee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ua.orlov.springcoregym.dto.trainee.TraineeRegister;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.dto.trainee.TraineeNamesResponse;
import ua.orlov.springcoregym.dto.trainee.UpdateTraineeRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TraineeMapper {

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    Trainee traineeRegisterToTrainee(TraineeRegister traineeRegister);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    UsernamePasswordUser traineeToUsernamePasswordUser(Trainee trainee);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "active", source = "active")
    Trainee updateTraineeRequestToTrainee(UpdateTraineeRequest request);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    TraineeNamesResponse traineeToTraineeNamesResponse(Trainee trainee);

    @Named("traineeListToTraineeNamesResponseList")
    List<TraineeNamesResponse> traineeListToTraineeNamesResponseList(List<Trainee> trainees);
}
