package ua.orlov.springcoregym.mapper.trainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ua.orlov.springcoregym.dto.trainer.TrainerRegister;
import ua.orlov.springcoregym.dto.trainer.TrainerResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerWorkload;
import ua.orlov.springcoregym.dto.trainer.UpdateTrainerRequest;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.exception.BusinessLogicException;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.ActionType;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.service.training.TrainingTypeService;

import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
public class TrainerMapper {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeMapper trainingTypeMapper;
    private final ObjectMapper objectMapper;

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
        trainer.setActive(request.isActive());
        return trainer;
    }

    public TrainerWorkload trainerToTrainerWorkload(Trainer trainer, Training training, ActionType actionType){
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainerUsername(trainer.getUsername());
        trainerWorkload.setTrainerFirstName(trainer.getFirstName());
        trainerWorkload.setTrainerLastName(trainer.getLastName());
        trainerWorkload.setTrainerIsActive(trainer.isActive());
        trainerWorkload.setTrainingDate(training.getTrainingDate());
        trainerWorkload.setTrainingDurationMinutes(training.getTrainingDurationMinutes());
        trainerWorkload.setActionType(actionType);
        return trainerWorkload;
    }

    public String trainerWorkloadToJson(TrainerWorkload trainerWorkload) {
        try {
            return objectMapper.writeValueAsString(trainerWorkload);
        } catch (Exception e){
            log.error(e);
            throw new BusinessLogicException("Serialization error", e);
        }
    }
}
