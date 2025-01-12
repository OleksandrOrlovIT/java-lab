package ua.orlov.springcoregym.service.training;

import ua.orlov.springcoregym.dto.training.CreateTrainingRequest;
import ua.orlov.springcoregym.dto.training.TraineeTrainingsRequest;
import ua.orlov.springcoregym.dto.training.TrainerTrainingRequest;
import ua.orlov.springcoregym.model.training.Training;

import java.util.List;

/**
 * Service interface for managing {@link Training} entities.
 */
public interface TrainingService {

    /**
     * Creates a new {@link Training} entity.
     *
     * @param training the training entity to create
     * @return the created {@link Training} entity
     * @throws NullPointerException if the training or any of its required fields are null
     */
    Training create(Training training);

    Training createFromCreateTrainingRequest(CreateTrainingRequest createTrainingRequest);

    /**
     * Retrieves a {@link Training} entity by its ID.
     *
     * @param id the ID of the training
     * @return the found {@link Training} entity
     * @throws java.util.NoSuchElementException if no training is found with the provided ID
     */
    Training getById(Long id);

    /**
     * Retrieves all {@link Training} entities.
     *
     * @return a list of all {@link Training} entities
     */
    List<Training> getAll();

    /**
     * Retrieves a list of training sessions based on criteria specified in a {@link TraineeTrainingsRequest}.
     *
     * @param request the criteria for retrieving trainings for a trainee
     * @return a list of trainings that match the specified criteria
     */
    List<Training> getTrainingsByCriteria(TraineeTrainingsRequest request);

    /**
     * Retrieves a list of training sessions based on criteria specified in a {@link TrainerTrainingRequest}.
     *
     * @param request the criteria for retrieving trainings for a trainer
     * @return a list of trainings that match the specified criteria
     */
    List<Training> getTrainingsByCriteria(TrainerTrainingRequest request);

    void deleteTrainingById(Long id);
}
