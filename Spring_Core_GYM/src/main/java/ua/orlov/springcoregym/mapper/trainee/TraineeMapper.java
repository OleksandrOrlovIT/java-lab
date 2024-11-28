package ua.orlov.springcoregym.mapper.trainee;

import org.springframework.stereotype.Component;
import ua.orlov.springcoregym.dto.trainee.TraineeRegister;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.dto.trainee.TraineeNamesResponse;
import ua.orlov.springcoregym.dto.trainee.UpdateTraineeRequest;

import java.util.List;

@Component
public class TraineeMapper {

    public Trainee traineeRegisterToTrainee(TraineeRegister traineeRegister){
        return Trainee.builder()
                .firstName(traineeRegister.getFirstName())
                .lastName(traineeRegister.getLastName())
                .dateOfBirth(traineeRegister.getDateOfBirth())
                .address(traineeRegister.getAddress())
                .build();
    }

    public UsernamePasswordUser traineeToUsernamePasswordUser(Trainee trainee){
        return new UsernamePasswordUser(trainee.getUsername(), trainee.getPassword());
    }

    public Trainee updateTraineeRequestToTrainee(UpdateTraineeRequest request){
        Trainee trainee = new Trainee();
        trainee.setUsername(request.getUsername());
        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());
        trainee.setActive(request.getIsActive());
        return trainee;
    }

    public TraineeNamesResponse traineeToTraineeNamesResponse(Trainee trainee){
        TraineeNamesResponse response = new TraineeNamesResponse();
        response.setUsername(trainee.getUsername());
        response.setFirstName(trainee.getFirstName());
        response.setLastName(trainee.getLastName());
        return response;
    }

    public List<TraineeNamesResponse> traineeListToTraineeNamesResponseList(List<Trainee> trainees){
        return trainees.stream()
                .map(this::traineeToTraineeNamesResponse)
                .toList();
    }
}
