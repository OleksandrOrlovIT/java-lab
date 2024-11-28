package ua.orlov.springcoregym.dao.impl.user.trainee;

import ua.orlov.springcoregym.dao.DaoUsernameFindable;
import ua.orlov.springcoregym.dto.trainee.TraineeTrainingDTO;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;

import java.util.List;

/**
 * DAO interface for managing {@link Trainee} entities with additional methods specific to trainees.
 */
public interface TraineeDao extends DaoUsernameFindable<Trainee, Long> {

    /**
     * Retrieves a list of {@link Training} entities associated with a trainee, filtered by the
     * provided {@link TraineeTrainingDTO}.
     *
     * @param traineeTrainingDTO DTO containing filtering criteria such as username, training type, and date range
     * @return a list of matching {@link Training} entities
     */
    List<Training> getTrainingsByTraineeTrainingDTO(TraineeTrainingDTO traineeTrainingDTO);

    /**
     * Deletes a {@link Trainee} entity by its username.
     *
     * @param username the username of the trainee to delete
     */
    void deleteByUsername(String username);

    List<Trainer> getTrainersByTraineeUsername(String username);
}
