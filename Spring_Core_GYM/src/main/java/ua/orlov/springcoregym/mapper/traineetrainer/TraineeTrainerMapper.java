package ua.orlov.springcoregym.mapper.traineetrainer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ua.orlov.springcoregym.dto.trainee.TraineeFullResponse;
import ua.orlov.springcoregym.dto.trainee.TraineeFullUsernameResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerFullResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerFullUsernameResponse;
import ua.orlov.springcoregym.dto.user.UsernameUser;
import ua.orlov.springcoregym.mapper.trainee.TraineeMapper;
import ua.orlov.springcoregym.mapper.trainer.TrainerMapper;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class TraineeTrainerMapper {

    private final TrainerMapper trainerMapper;
    private final TraineeMapper traineeMapper;
    private final TrainingTypeMapper trainingTypeMapper;

    public TraineeFullResponse traineeToTraineeFullResponse(Trainee trainee){
        TraineeFullResponse response = new TraineeFullResponse();
        response.setFirstName(trainee.getFirstName());
        response.setLastName(trainee.getLastName());
        response.setDateOfBirth(trainee.getDateOfBirth());
        response.setAddress(trainee.getAddress());
        response.setActive(trainee.isActive());
        response.setTrainers(trainerMapper.trainersListToTrainerResponseList(trainee.getTrainers()));

        return response;
    }

    public TraineeFullUsernameResponse traineeToTraineeFullUsernameResponse(Trainee trainee){
        TraineeFullUsernameResponse response = new TraineeFullUsernameResponse();
        response.setUsername(trainee.getUsername());
        response.setFirstName(trainee.getFirstName());
        response.setLastName(trainee.getLastName());
        response.setDateOfBirth(trainee.getDateOfBirth());
        response.setAddress(trainee.getAddress());
        response.setActive(trainee.isActive());
        response.setTrainers(trainerMapper.trainersListToTrainerResponseList(trainee.getTrainers()));

        return response;
    }

    public TrainerFullResponse trainerToTrainerFullResponse(Trainer trainer){
        TrainerFullResponse response = new TrainerFullResponse();
        response.setFirstName(trainer.getFirstName());
        response.setLastName(trainer.getLastName());
        response.setSpecialization(trainingTypeMapper.trainingTypeToTrainingTypeResponse(trainer.getSpecialization()));
        response.setActive(trainer.isActive());
        response.setTrainees(traineeMapper.traineeListToTraineeNamesResponseList(trainer.getTrainees()));
        return response;
    }

    public TrainerFullUsernameResponse trainerToTrainerFullUsernameResponse(Trainer trainer){
        TrainerFullUsernameResponse response = new TrainerFullUsernameResponse();
        response.setUsername(trainer.getUsername());
        response.setFirstName(trainer.getFirstName());
        response.setLastName(trainer.getLastName());
        response.setSpecialization(trainingTypeMapper.trainingTypeToTrainingTypeResponse(trainer.getSpecialization()));
        response.setActive(trainer.isActive());
        response.setTrainees(traineeMapper.traineeListToTraineeNamesResponseList(trainer.getTrainees()));
        return response;
    }
}
