package ua.orlov.springcoregym.mapper.training;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ua.orlov.springcoregym.dto.training.CreateTrainingRequest;
import ua.orlov.springcoregym.dto.training.TrainingFullResponse;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.service.training.TrainingTypeService;
import ua.orlov.springcoregym.service.user.trainee.TraineeService;
import ua.orlov.springcoregym.service.user.trainer.TrainerService;

import java.util.List;

@Component
@AllArgsConstructor
public class TrainingMapper {

    private final TrainingTypeMapper trainingTypeMapper;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;

    public TrainingFullResponse trainingToTrainingFullResponse(Training training) {
        TrainingFullResponse response = new TrainingFullResponse();
        response.setTrainingName(training.getTrainingName());
        response.setTrainingDate(training.getTrainingDate());
        response.setTrainingType(trainingTypeMapper.trainingTypeToTrainingTypeResponse(training.getTrainingType()));
        response.setTrainingDurationMinutes(training.getTrainingDurationMinutes());
        response.setTrainerName(training.getTrainer().getUsername());
        return response;
    }

    public List<TrainingFullResponse> trainingListToTrainingFullResponseList(List<Training> trainings) {
        return trainings.stream()
                .map(this::trainingToTrainingFullResponse)
                .toList();
    }

    public Training createTrainingRequestToTraining(CreateTrainingRequest request) {
        Training training = new Training();
        training.setTrainingName(request.getTrainingName());
        training.setTrainingDate(request.getTrainingDate());
        training.setTrainingDurationMinutes(request.getTrainingDurationMinutes());
        training.setTrainee(traineeService.getByUsername(request.getTraineeUsername()));
        training.setTrainer(trainerService.getByUsername(request.getTrainerUsername()));
        training.setTrainingType(trainingTypeService.getById(request.getTrainingTypeId()));
        return training;
    }
}
