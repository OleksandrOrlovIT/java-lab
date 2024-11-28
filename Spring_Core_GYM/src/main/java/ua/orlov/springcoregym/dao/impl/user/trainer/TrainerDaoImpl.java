package ua.orlov.springcoregym.dao.impl.user.trainer;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import ua.orlov.springcoregym.dao.AbstractDao;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.model.page.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Log4j2
@Repository
public class TrainerDaoImpl extends AbstractDao<Trainer, Long> implements TrainerDao {

    private final String FIND_BY_USERNAME_QUERY;
    private final String GET_TRAININGS_BY_DATE_AND_USERNAME_QUERY;
    private final String GET_TRAINERS_WITHOUT_PASSED_TRAINEE_QUERY;
    private final String FIND_BY_IDS_QUERY;
    private final String GET_TRAINEES_BY_TRAINER_USERNAME_QUERY;
    private final String FIND_BY_USERNAMES_QUERY;

    public TrainerDaoImpl() {
        FIND_BY_USERNAME_QUERY = "SELECT t FROM " + getEntityClass().getSimpleName() + " t WHERE t.username = :username";
        GET_TRAININGS_BY_DATE_AND_USERNAME_QUERY = """
                SELECT tr FROM Training tr
                JOIN tr.trainer t
                WHERE t.username = :username
                AND tr.trainingDate BETWEEN :startDate AND :endDate
                """;
        GET_TRAINERS_WITHOUT_PASSED_TRAINEE_QUERY = "SELECT tr FROM Trainer tr WHERE :trainee NOT MEMBER OF tr.trainees";
        FIND_BY_IDS_QUERY = "SELECT t FROM " + getEntityClass().getSimpleName() + " t WHERE t.id IN :ids";
        GET_TRAINEES_BY_TRAINER_USERNAME_QUERY = """
        SELECT tr FROM Trainer t
        JOIN t.trainees tr
        WHERE t.username = :username
        """;
        FIND_BY_USERNAMES_QUERY = "SELECT t FROM " + getEntityClass().getSimpleName() + " t WHERE t.username IN :usernames";
    }

    @Override
    protected Class<Trainer> getEntityClass() {
        return Trainer.class;
    }

    @Override
    public Optional<Trainer> getByUsername(String username) {
        try {
            TypedQuery<Trainer> query = getEntityManager().createQuery(FIND_BY_USERNAME_QUERY, Trainer.class);
            query.setParameter("username", username);

            Trainer trainer = query.getSingleResult();
            return Optional.of(trainer);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Training> getTrainingsByDateAndUsername(LocalDate startDate, LocalDate endDate, String userName) {
        TypedQuery<Training> query = getEntityManager().createQuery(GET_TRAININGS_BY_DATE_AND_USERNAME_QUERY, Training.class);
        query.setParameter("username", userName);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        return query.getResultList();
    }

    @Override
    public List<Trainer> getTrainersWithoutPassedTrainee(Trainee trainee, Pageable pageable) {
        TypedQuery<Trainer> query = getEntityManager().createQuery(GET_TRAINERS_WITHOUT_PASSED_TRAINEE_QUERY, Trainer.class);
        query.setParameter("trainee", trainee);
        query.setFirstResult(pageable.getPageNumber());
        query.setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }

    @Override
    public List<Trainer> getByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<Trainer> query = getEntityManager().createQuery(FIND_BY_IDS_QUERY, Trainer.class);
        query.setParameter("ids", ids);

        return query.getResultList();
    }

    @Override
    public List<Trainee> getTraineesByTrainerUsername(String username) {
        TypedQuery<Trainee> query = getEntityManager().createQuery(GET_TRAINEES_BY_TRAINER_USERNAME_QUERY, Trainee.class);
        query.setParameter("username", username);

        return query.getResultList();
    }

    @Override
    public List<Trainer> getByUsernames(List<String> trainerUsernames) {
        if (trainerUsernames.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<Trainer> query = getEntityManager().createQuery(FIND_BY_USERNAMES_QUERY, Trainer.class);
        query.setParameter("usernames", trainerUsernames);

        return query.getResultList();
    }
}
