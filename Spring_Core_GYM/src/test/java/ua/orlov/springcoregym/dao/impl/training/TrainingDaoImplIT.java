package ua.orlov.springcoregym.dao.impl.training;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.orlov.springcoregym.dao.impl.user.trainee.TraineeDao;
import ua.orlov.springcoregym.dao.impl.user.trainer.TrainerDao;
import ua.orlov.springcoregym.dto.training.TraineeTrainingsRequest;
import ua.orlov.springcoregym.dto.training.TrainerTrainingRequest;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.training.TrainingType;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/sql/training/populate_trainings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/prune_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class TrainingDaoImplIT {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TraineeDao traineeDao;

    private Training testTraining;
    private TrainingType testTrainingType;
    private Trainer testTrainer;
    private Trainee testTrainee;

    private static final String TRAINING_NAME = "testTrainingName";

    @BeforeEach
    void setUpEntities() {
        Assertions.assertEquals(1, traineeDao.getAll().size());
        Assertions.assertEquals(1, trainingTypeDao.getAll().size());
        Assertions.assertEquals(1, trainerDao.getAll().size());
        Assertions.assertEquals(2, trainingDao.getAll().size());

        testTrainingType = trainingTypeDao.getAll().get(0);

        testTrainee = traineeDao.getAll().get(0);

        testTrainer = this.trainerDao.getAll().get(0);

        testTraining = Training.builder()
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName(TRAINING_NAME)
                .trainingType(testTrainingType)
                .trainingDate(LocalDate.MIN)
                .trainingDurationMinutes(10)
                .build();
    }

    @Test
    @Transactional
    void createTraining() {
        testTraining = trainingDao.create(testTraining);

        assertNotNull(testTraining);
        assertNotNull(testTraining.getId());
    }

    @Test
    void getByIdTraining() {
        testTraining = trainingDao.getAll().get(0);
        Optional<Training> foundTraining = trainingDao.getById(testTraining.getId());

        assertTrue(foundTraining.isPresent());
        assertEquals(testTraining, foundTraining.get());
    }

    @Test
    @Transactional
    void deleteTraining() {
        testTraining = trainingDao.create(testTraining);

        trainingDao.deleteById(testTraining.getId());
        Optional<Training> deleted = trainingDao.getById(testTraining.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void deleteNonExistentTraining() {
        assertDoesNotThrow(() -> trainingDao.deleteById(-1L));
    }

    @Test
    @Transactional
    void updateTraining() {
        Training savedTraining = trainingDao.getAll().get(0);

        String delim = "1";

        TrainingType testTrainingTypeForUpdate = TrainingType.builder()
                .trainingTypeName(testTrainingType.getTrainingTypeName() + delim)
                .build();

        Trainer testTrainerForUpdate = Trainer.builder()
                .username(testTrainer.getUsername() + delim)
                .firstName(testTrainer.getFirstName() + delim)
                .lastName(testTrainer.getLastName() + delim)
                .password(testTrainer.getPassword() + delim)
                .active(!testTrainer.isActive())
                .specialization(testTrainingType)
                .build();

        Trainee testTraineeForUpdate = Trainee.builder()
                .username(testTrainee.getUsername() + delim)
                .firstName(testTrainee.getFirstName() + delim)
                .lastName(testTrainee.getLastName() + delim)
                .password(testTrainee.getPassword() + delim)
                .active(!testTrainee.isActive())
                .build();

        testTrainingTypeForUpdate = trainingTypeDao.create(testTrainingTypeForUpdate);
        testTrainerForUpdate = trainerDao.create(testTrainerForUpdate);
        testTraineeForUpdate = traineeDao.create(testTraineeForUpdate);

        Training diffTraining = Training.builder()
                .id(savedTraining.getId())
                .trainee(testTraineeForUpdate)
                .trainer(testTrainerForUpdate)
                .trainingName(TRAINING_NAME + delim)
                .trainingType(testTrainingTypeForUpdate)
                .trainingDate(LocalDate.MIN.plusDays(1))
                .trainingDurationMinutes(10)
                .build();

        Training updated = trainingDao.update(diffTraining);

        assertEquals(testTraineeForUpdate, updated.getTrainee());
        Assertions.assertEquals(testTrainerForUpdate, updated.getTrainer());
        assertEquals(testTrainingTypeForUpdate, updated.getTrainingType());
        assertEquals(TRAINING_NAME + delim, updated.getTrainingName());
        assertEquals(LocalDate.MIN.plusDays(1), updated.getTrainingDate());
        assertEquals(10, updated.getTrainingDurationMinutes());
    }

    @Test
    void getAllTrainings() {
        List<Training> trainingList = trainingDao.getAll();

        assertNotNull(trainingList);
        assertEquals(2, trainingList.size());
    }

    @Test
    void getTrainingsByCriteriaTraineeTrainingsRequestThenSuccess(){
        TraineeTrainingsRequest traineeTrainingsRequest = new TraineeTrainingsRequest();
        traineeTrainingsRequest.setUsername("testtrainee");
        traineeTrainingsRequest.setStartDate(LocalDate.parse("2024-10-01"));
        traineeTrainingsRequest.setEndDate(LocalDate.parse("2024-10-31"));
        traineeTrainingsRequest.setTrainerUsername("testtrainer");
        traineeTrainingsRequest.setTrainingTypeId(trainingTypeDao.getAll().get(0).getId());

        List<Training> trainings = trainingDao.getTrainingsByCriteria(traineeTrainingsRequest);
        assertNotNull(trainings);
        assertEquals(2, trainings.size());
    }

    @Test
    void getTrainingsByCriteriaTraineeUsernameThenSuccess(){
        TraineeTrainingsRequest traineeTrainingsRequest = new TraineeTrainingsRequest();
        traineeTrainingsRequest.setUsername("testtrainee");

        List<Training> trainings = trainingDao.getTrainingsByCriteria(traineeTrainingsRequest);
        assertNotNull(trainings);
        assertEquals(2, trainings.size());
    }

    @Test
    void getTrainingsByCriteriaTraineeUsernameStartDateThenSuccess(){
        TraineeTrainingsRequest traineeTrainingsRequest = new TraineeTrainingsRequest();
        traineeTrainingsRequest.setUsername("testtrainee");
        traineeTrainingsRequest.setStartDate(LocalDate.parse("2024-10-01"));

        List<Training> trainings = trainingDao.getTrainingsByCriteria(traineeTrainingsRequest);
        assertNotNull(trainings);
        assertEquals(2, trainings.size());
    }

    @Test
    void getTrainingsByCriteriaTrainerTrainingsRequestThenSuccess(){
        TrainerTrainingRequest trainerTrainingRequest = new TrainerTrainingRequest();
        trainerTrainingRequest.setUsername("testtrainer");
        trainerTrainingRequest.setStartDate(LocalDate.parse("2024-10-01"));
        trainerTrainingRequest.setEndDate(LocalDate.parse("2024-10-31"));
        trainerTrainingRequest.setTraineeUsername("testtrainee");

        List<Training> trainings = trainingDao.getTrainingsByCriteria(trainerTrainingRequest);
        assertNotNull(trainings);
        assertEquals(2, trainings.size());
    }

    @Test
    void getTrainingsByCriteriaTrainerUsernameThenSuccess(){
        TrainerTrainingRequest trainerTrainingRequest = new TrainerTrainingRequest();
        trainerTrainingRequest.setUsername("testtrainer");

        List<Training> trainings = trainingDao.getTrainingsByCriteria(trainerTrainingRequest);
        assertNotNull(trainings);
        assertEquals(2, trainings.size());
    }

    @Test
    void getTrainingsByCriteriaTrainerUsernameStartDateThenSuccess(){
        TrainerTrainingRequest trainerTrainingRequest = new TrainerTrainingRequest();
        trainerTrainingRequest.setUsername("testtrainer");
        trainerTrainingRequest.setStartDate(LocalDate.parse("2024-10-01"));

        List<Training> trainings = trainingDao.getTrainingsByCriteria(trainerTrainingRequest);
        assertNotNull(trainings);
        assertEquals(2, trainings.size());
    }
}
