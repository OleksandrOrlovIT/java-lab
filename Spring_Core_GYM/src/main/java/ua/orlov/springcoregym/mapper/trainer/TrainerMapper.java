package ua.orlov.springcoregym.mapper.trainer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ua.orlov.springcoregym.dto.trainer.TrainerRegister;
import ua.orlov.springcoregym.dto.trainer.TrainerResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerWorkload;
import ua.orlov.springcoregym.dto.trainer.UpdateTrainerRequest;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.ActionType;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.service.training.TrainingTypeService;

import java.util.List;

@Component
@AllArgsConstructor
public class TrainerMapper {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeMapper trainingTypeMapper;

    public Trainer trainerRegisterToTrainer(TrainerRegister trainerRegister){
        Trainer trainer = new Trainer();
        trainer.setFirstName(trainerRegister.getFirstName());
        trainer.setLastName(trainerRegister.getLastName());
        trainer.setSpecialization(trainingTypeService.getById(trainerRegister.getSpecializationId()));
        return trainer;
    }

    public UsernamePasswordUser trainerToUsernamePasswordUser(Trainer trainer){
        return new UsernamePasswordUser(trainer.getUsername(), trainer.getPassword());
    }

    public TrainerResponse trainerToTrainerResponse(Trainer trainer){
        TrainerResponse trainerResponse = new TrainerResponse();
        trainerResponse.setUsername(trainer.getUsername());
        trainerResponse.setFirstName(trainer.getFirstName());
        trainerResponse.setLastName(trainer.getLastName());
        trainerResponse.setSpecialization(trainingTypeMapper.trainingTypeToTrainingTypeResponse(trainer.getSpecialization()));
        return trainerResponse;
    }

    public List<TrainerResponse> trainersListToTrainerResponseList(List<Trainer> trainers){
        return trainers.stream()
                .map(this::trainerToTrainerResponse)
                .toList();
    }

    public Trainer updateTrainerRequestToTrainer(UpdateTrainerRequest request){
        Trainer trainer = new Trainer();
        trainer.setUsername(request.getUsername());
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setSpecialization(trainingTypeService.getById(request.getSpecializationId()));
        trainer.setActive(request.getIsActive());
        return trainer;
    }

    public TrainerWorkload trainerToTrainerWorkload(Trainer trainer, Training training, ActionType actionType){
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setUsername(trainer.getUsername());
        trainerWorkload.setFirstName(trainer.getFirstName());
        trainerWorkload.setLastName(trainer.getLastName());
        trainerWorkload.setIsActive(trainer.isActive());
        trainerWorkload.setTrainingDate(training.getTrainingDate());
        trainerWorkload.setTrainingDuration(training.getTrainingDuration());
        trainerWorkload.setActionType(actionType);
        return trainerWorkload;
    }
}
