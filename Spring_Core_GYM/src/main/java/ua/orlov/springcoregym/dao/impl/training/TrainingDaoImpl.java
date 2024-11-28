package ua.orlov.springcoregym.dao.impl.training;

import jakarta.persistence.TypedQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import ua.orlov.springcoregym.dao.AbstractDao;
import ua.orlov.springcoregym.dto.training.TraineeTrainingsRequest;
import ua.orlov.springcoregym.dto.training.TrainerTrainingRequest;
import ua.orlov.springcoregym.model.training.Training;

import java.util.List;


@Log4j2
@Repository
public class TrainingDaoImpl extends AbstractDao<Training, Long> implements TrainingDao {

    private final String GET_TRAININGS_BY_TRAINEE_TRAININGS_REQUEST;
    private final String GET_TRAININGS_BY_TRAINER_TRAININGS_REQUEST;

    public TrainingDaoImpl() {
        GET_TRAININGS_BY_TRAINEE_TRAININGS_REQUEST = "SELECT tr FROM " + getEntityClass().getSimpleName()
                + " tr JOIN tr.trainee t WHERE t.username = :username";
        GET_TRAININGS_BY_TRAINER_TRAININGS_REQUEST = "SELECT tr FROM " + getEntityClass().getSimpleName()
                + " tr JOIN tr.trainer t WHERE t.username = :username";
    }

    @Override
    protected Class<Training> getEntityClass() {
        return Training.class;
    }

    @Override
    public List<Training> getTrainingsByCriteria(TraineeTrainingsRequest request) {
        StringBuilder queryBuilder = getTraineeStringBuilder(request);

        TypedQuery<Training> query = getEntityManager().createQuery(queryBuilder.toString(), Training.class);
        query.setParameter("username", request.getUsername());

        if (request.getStartDate() != null && request.getEndDate() != null) {
            query.setParameter("startDate", request.getStartDate());
            query.setParameter("endDate", request.getEndDate());
        }

        if (request.getTrainerUsername() != null) {
            query.setParameter("trainerUsername", request.getTrainerUsername());
        }

        if (request.getTrainingTypeId() != null) {
            query.setParameter("trainingTypeId", request.getTrainingTypeId());
        }

        return query.getResultList();
    }

    private StringBuilder getTraineeStringBuilder(TraineeTrainingsRequest request) {
        StringBuilder queryBuilder = new StringBuilder(GET_TRAININGS_BY_TRAINEE_TRAININGS_REQUEST);

        if (request.getStartDate() != null && request.getEndDate() != null) {
            queryBuilder.append(" AND tr.trainingDate BETWEEN :startDate AND :endDate");
        }

        if (request.getTrainerUsername() != null) {
            queryBuilder.append(" AND EXISTS (SELECT 1 FROM tr.trainer trn WHERE trn.username = :trainerUsername)");
        }

        if (request.getTrainingTypeId() != null) {
            queryBuilder.append(" AND tr.trainingType.id = :trainingTypeId");
        }

        return queryBuilder;
    }

    @Override
    public List<Training> getTrainingsByCriteria(TrainerTrainingRequest request) {
        StringBuilder queryBuilder = getTrainerStringBuilder(request);

        TypedQuery<Training> query = getEntityManager().createQuery(queryBuilder.toString(), Training.class);
        query.setParameter("username", request.getUsername());

        if (request.getStartDate() != null && request.getEndDate() != null) {
            query.setParameter("startDate", request.getStartDate());
            query.setParameter("endDate", request.getEndDate());
        }

        if (request.getTraineeUsername() != null) {
            query.setParameter("traineeUsername", request.getTraineeUsername());
        }

        return query.getResultList();
    }

    private StringBuilder getTrainerStringBuilder(TrainerTrainingRequest request) {
        StringBuilder queryBuilder = new StringBuilder(GET_TRAININGS_BY_TRAINER_TRAININGS_REQUEST);

        if (request.getStartDate() != null && request.getEndDate() != null) {
            queryBuilder.append(" AND tr.trainingDate BETWEEN :startDate AND :endDate");
        }

        if (request.getTraineeUsername() != null) {
            queryBuilder.append(" AND EXISTS (SELECT 1 FROM tr.trainee trn WHERE trn.username = :traineeUsername)");
        }

        return queryBuilder;
    }
}
