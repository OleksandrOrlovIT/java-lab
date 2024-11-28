package ua.orlov.springcoregym.service.training;

import ua.orlov.springcoregym.model.training.TrainingType;

import java.util.List;

/**
 * Service interface for managing {@link TrainingType} entities.
 */
public interface TrainingTypeService {

    /**
     * Retrieves a {@link TrainingType} entity by its ID.
     *
     * @param id the ID of the training type
     * @return the found {@link TrainingType} entity
     * @throws java.util.NoSuchElementException if no training type is found with the provided ID
     */
    TrainingType getById(Long id);

    /**
     * Retrieves all {@link TrainingType} entities.
     *
     * @return a list of all {@link TrainingType} entities
     */
    List<TrainingType> getAll();
}
