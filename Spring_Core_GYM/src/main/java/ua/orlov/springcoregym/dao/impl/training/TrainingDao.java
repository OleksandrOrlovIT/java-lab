package ua.orlov.springcoregym.dao.impl.training;

import ua.orlov.springcoregym.dao.Dao;
import ua.orlov.springcoregym.dto.training.TraineeTrainingsRequest;
import ua.orlov.springcoregym.dto.training.TrainerTrainingRequest;
import ua.orlov.springcoregym.model.training.Training;

import java.util.List;

/**
 * DAO interface for managing {@link Training} entities.
 */
public interface TrainingDao extends Dao<Training, Long> {

    List<Training> getTrainingsByCriteria(TraineeTrainingsRequest request);

    List<Training> getTrainingsByCriteria(TrainerTrainingRequest request);
}
