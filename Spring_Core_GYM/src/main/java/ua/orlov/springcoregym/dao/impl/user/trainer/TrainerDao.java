package ua.orlov.springcoregym.dao.impl.user.trainer;

import ua.orlov.springcoregym.dao.DaoUsernameFindable;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.model.page.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * DAO interface for managing {@link Trainer} entities with additional methods specific to trainers.
 */
public interface TrainerDao extends DaoUsernameFindable<Trainer, Long> {

    /**
     * Retrieves a list of {@link Training} entities conducted by a trainer, filtered by the date range and username.
     *
     * @param startDate the start date of the training period
     * @param endDate   the end date of the training period
     * @param userName  the username of the trainer
     * @return a list of matching {@link Training} entities
     */
    List<Training> getTrainingsByDateAndUsername(LocalDate startDate, LocalDate endDate, String userName);

    /**
     * Retrieves a list of {@link Trainer} entities who have not trained the specified {@link Trainee}.
     *
     * @param trainee  the trainee who has not been trained by the trainers
     * @param pageable pagination details for the query
     * @return a list of trainers who have not trained the given trainee
     */
    List<Trainer> getTrainersWithoutPassedTrainee(Trainee trainee, Pageable pageable);

    /**
     * Retrieves a list of {@link Trainer} entities by their IDs.
     *
     * @param trainerIds the list of trainer IDs
     * @return a list of matching {@link Trainer} entities
     */
    List<Trainer> getByIds(List<Long> trainerIds);

    List<Trainee> getTraineesByTrainerUsername(String username);

    List<Trainer> getByUsernames(List<String> trainerUsernames);
}
