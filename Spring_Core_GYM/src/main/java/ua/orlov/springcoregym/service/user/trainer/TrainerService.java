package ua.orlov.springcoregym.service.user.trainer;

import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.model.page.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing {@link Trainer} entities and related operations.
 */
public interface TrainerService {

    /**
     * Creates a new {@link Trainer} entity.
     *
     * @param trainer the trainer entity to create
     * @return the created {@link Trainer} entity
     * @throws java.lang.NullPointerException if required fields are null
     */
    Trainer create(Trainer trainer);

    /**
     * Retrieves a {@link Trainer} entity by its ID.
     *
     * @param id the ID of the trainer
     * @return the found {@link Trainer} entity
     * @throws java.util.NoSuchElementException if no trainer is found with the provided ID
     */
    Trainer select(Long id);

    /**
     * Retrieves all {@link Trainer} entities.
     *
     * @return a list of all {@link Trainer} entities
     */
    List<Trainer> getAll();

    /**
     * Updates an existing {@link Trainer} entity.
     *
     * @param trainer the trainer entity to update
     * @return the updated {@link Trainer} entity
     * @throws java.lang.IllegalArgumentException if the isActive field is changed during the update
     */
    Trainer update(Trainer trainer);

    /**
     * Checks if the provided username and password match for a {@link Trainer}.
     *
     * @param username the trainer's username
     * @param password the trainer's password
     * @return true if the password matches, false otherwise
     * @throws java.lang.IllegalArgumentException if no trainer is found with the provided username
     */
    boolean isUserNameMatchPassword(String username, String password);

    /**
     * Changes the password of the given {@link Trainer}.
     *
     * @param trainer the trainer entity
     * @param newPassword the new password to set
     * @return the updated {@link Trainer} entity with the new password
     * @throws java.lang.IllegalArgumentException if the current password is incorrect
     */
    Trainer changePassword(Trainer trainer, String newPassword);

    /**
     * Activates a {@link Trainer}.
     *
     * @param trainerId the ID of the trainer to activate
     * @return the activated {@link Trainer} entity
     * @throws java.lang.IllegalArgumentException if the trainer is already active
     */
    Trainer activateTrainer(Long trainerId);

    /**
     * Deactivates a {@link Trainer}.
     *
     * @param trainerId the ID of the trainer to deactivate
     * @return the deactivated {@link Trainer} entity
     * @throws java.lang.IllegalArgumentException if the trainer is already deactivated
     */
    Trainer deactivateTrainer(Long trainerId);

    /**
     * Retrieves the list of trainings between two dates for a specific trainer.
     *
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @param userName the trainer's username
     * @return a list of {@link Training} entities for the specified period
     */
    List<Training> getTrainingsByDate(LocalDate startDate, LocalDate endDate, String userName);

    /**
     * Retrieves a paginated list of trainers who have no connection with a given trainee.
     *
     * @param traineeUsername the username of the trainee
     * @param pageable the paging information
     * @return a list of {@link Trainer} entities without the given trainee
     * @throws java.lang.IllegalArgumentException if no trainee is found with the provided username
     */
    List<Trainer> getTrainersWithoutPassedTrainee(String traineeUsername, Pageable pageable);

    /**
     * Authenticates a trainer by their username and password.
     *
     * @param userName the trainer's username
     * @param password the trainer's password
     * @return the authenticated {@link Trainer} entity
     * @throws java.lang.IllegalArgumentException if the username or password is incorrect
     */
    Trainer authenticateTrainer(String userName, String password);

    /**
     * Retrieves a {@link Trainer} by their username.
     *
     * @param trainerUserName the username of the trainer
     * @return the found {@link Trainer} entity
     * @throws java.lang.IllegalArgumentException if no trainer is found with the provided username
     */
    Trainer getByUsername(String trainerUserName);

    /**
     * Retrieves a trainer by username along with their assigned trainees.
     *
     * @param trainerUsername the username of the trainer to retrieve
     * @return the trainer entity with trainees included
     */
    Trainer getByUserNameWithTrainees(String trainerUsername);

    /**
     * Activates or deactivates a trainer based on the specified status.
     *
     * @param trainerUsername the username of the trainer to update
     * @param isActive true to activate the trainer; false to deactivate
     */
    void activateDeactivateTrainer(String trainerUsername, boolean isActive);
}
