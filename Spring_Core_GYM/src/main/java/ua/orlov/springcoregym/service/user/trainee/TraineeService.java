package ua.orlov.springcoregym.service.user.trainee;

import ua.orlov.springcoregym.dto.trainee.TraineeTrainingDTO;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;

import java.util.List;

/**
 * Service interface for managing {@link Trainee} entities and their related operations.
 */
public interface TraineeService {

    /**
     * Creates a new {@link Trainee} entity.
     *
     * @param trainee the trainee entity to create
     * @return the created {@link Trainee} entity
     * @throws NullPointerException if the trainee or required fields are null
     */
    Trainee create(Trainee trainee);

    /**
     * Retrieves a {@link Trainee} entity by its ID.
     *
     * @param id the ID of the trainee
     * @return the found {@link Trainee} entity
     * @throws java.util.NoSuchElementException if no trainee is found with the provided ID
     */
    Trainee select(Long id);

    /**
     * Retrieves all {@link Trainee} entities.
     *
     * @return a list of all {@link Trainee} entities
     */
    List<Trainee> getAll();

    /**
     * Updates an existing {@link Trainee} entity.
     *
     * @param trainee the trainee entity to update
     * @return the updated {@link Trainee} entity
     * @throws ua.orlov.springcoregym.exception.BusinessLogicException if the isActive field is changed during update
     */
    Trainee update(Trainee trainee);

    /**
     * Deletes a trainee by their username.
     *
     * @param userName the username of the trainee to delete
     */
    void deleteByUsername(String userName);

    /**
     * Checks if the provided username and password match.
     *
     * @param username the trainee's username
     * @param password the trainee's password
     * @return true if the password matches, false otherwise
     * @throws ua.orlov.springcoregym.exception.BusinessLogicException if no trainee is found with the provided username
     */
    boolean isUserNameMatchPassword(String username, String password);

    /**
     * Changes the password of the given {@link Trainee}.
     *
     * @param trainee the trainee entity
     * @param newPassword the new password to set
     * @return the updated {@link Trainee} entity with the new password
     * @throws ua.orlov.springcoregym.exception.BusinessLogicException if the current password is incorrect
     */
    Trainee changePassword(Trainee trainee, String newPassword);

    /**
     * Activates a {@link Trainee}.
     *
     * @param traineeId the ID of the trainee to activate
     * @return the activated {@link Trainee} entity
     * @throws ua.orlov.springcoregym.exception.BusinessLogicException if the trainee is already active
     */
    Trainee activateTrainee(Long traineeId);

    /**
     * Deactivates a {@link Trainee}.
     *
     * @param traineeId the ID of the trainee to deactivate
     * @return the deactivated {@link Trainee} entity
     * @throws ua.orlov.springcoregym.exception.BusinessLogicException if the trainee is already deactivated
     */
    Trainee deactivateTrainee(Long traineeId);

    /**
     * Retrieves a list of trainings for a specific trainee based on the given {@link TraineeTrainingDTO}.
     *
     * @param traineeTrainingDTO the DTO containing the trainee's information
     * @return a list of {@link Training} entities
     */
    List<Training> getTrainingsByTraineeTrainingDTO(TraineeTrainingDTO traineeTrainingDTO);

    /**
     * Authenticates a trainee by their username and password.
     *
     * @param userName the trainee's username
     * @param password the trainee's password
     * @return the authenticated {@link Trainee} entity
     * @throws ua.orlov.springcoregym.exception.BusinessLogicException if the username or password is incorrect
     */
    Trainee authenticateTrainee(String userName, String password);

    /**
     * Retrieves a {@link Trainee} by their username.
     *
     * @param traineeUsername the username of the trainee
     * @return the found {@link Trainee} entity
     * @throws ua.orlov.springcoregym.exception.BusinessLogicException if no trainee is found with the provided username
     */
    Trainee getByUsername(String traineeUsername);

    /**
     * Updates the list of trainers assigned to a trainee by trainee ID.
     *
     * @param traineeId the ID of the trainee to update
     * @param trainerIds the list of trainer IDs to assign to the trainee
     * @return the updated list of trainers assigned to the trainee
     */
    List<Trainer> updateTraineeTrainers(Long traineeId, List<Long> trainerIds);

    /**
     * Updates the list of trainers assigned to a trainee by trainee username.
     *
     * @param traineeUsername the username of the trainee to update
     * @param trainerUsernames the list of trainer usernames to assign to the trainee
     * @return the updated list of trainers assigned to the trainee
     */
    List<Trainer> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames);

    /**
     * Retrieves a trainee by username along with their assigned trainers.
     *
     * @param traineeUsername the username of the trainee to retrieve
     * @return the trainee entity with trainers included
     */
    Trainee getByUserNameWithTrainers(String traineeUsername);

    /**
     * Activates or deactivates a trainee based on the specified status.
     *
     * @param traineeUsername the username of the trainee to update
     * @param isActive true to activate the trainee; false to deactivate
     */
    void activateDeactivateTrainee(String traineeUsername, boolean isActive);
}
