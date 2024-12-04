package ua.orlov.springcoregym.dao.impl.training;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.orlov.springcoregym.model.training.TrainingType;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/sql/training_type/populate_training_types.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/training_type/prune_training_types.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TrainingTypeDaoImplIT {

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    private TrainingType testTrainingType;

    private static final String TRAINING_TYPE_NAME = "testTrainingTypeName";

    @BeforeEach
    void setUp() {
        testTrainingType = TrainingType.builder()
                .trainingTypeName(TRAINING_TYPE_NAME)
                .build();

        Assertions.assertEquals(2, trainingTypeDao.getAll().size());
    }

    @Test
    void createTrainingType() {
        testTrainingType = trainingTypeDao.create(testTrainingType);

        assertNotNull(testTrainingType);
        assertNotNull(testTrainingType.getId());
    }

    @Test
    void getByIdTrainingType() {
        testTrainingType = trainingTypeDao.getAll().get(0);
        Optional<TrainingType> foundTrainingType = trainingTypeDao.getById(testTrainingType.getId());

        assertTrue(foundTrainingType.isPresent());
        assertEquals(testTrainingType, foundTrainingType.get());
    }

    @Test
    void deleteTrainingType() {
        TrainingType trainingType = trainingTypeDao.getAll().get(0);

        trainingTypeDao.deleteById(trainingType.getId());
        Optional<TrainingType> deleted = trainingTypeDao.getById(trainingType.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void deleteNonExistentTrainingType() {
        assertDoesNotThrow(() -> trainingTypeDao.deleteById(-1L));
    }

    @Test
    void updateTrainingType() {
        TrainingType savedTrainingType = trainingTypeDao.getAll().get(0);

        String delim = "1";

        TrainingType diffTrainingType = TrainingType.builder()
                .trainingTypeName(savedTrainingType.getTrainingTypeName() + delim)
                .build();

        TrainingType updated = trainingTypeDao.update(diffTrainingType);

        assertNotEquals(updated, savedTrainingType);
        assertEquals(updated.getTrainingTypeName(), savedTrainingType.getTrainingTypeName() + delim);
    }

    @Test
    void getAllTrainingTypes() {
        List<TrainingType> trainingTypeList = trainingTypeDao.getAll();

        assertNotNull(trainingTypeList);
        assertEquals(2, trainingTypeList.size());
    }
}
