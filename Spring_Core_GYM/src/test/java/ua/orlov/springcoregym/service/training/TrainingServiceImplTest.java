package ua.orlov.springcoregym.service.training;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.springcoregym.dao.impl.training.TrainingDao;
import ua.orlov.springcoregym.dto.trainer.TrainerWorkload;
import ua.orlov.springcoregym.dto.training.TraineeTrainingsRequest;
import ua.orlov.springcoregym.dto.training.TrainerTrainingRequest;
import ua.orlov.springcoregym.mapper.trainer.TrainerMapper;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.training.TrainingType;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.service.messages.MessageSender;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private MessageSender messageSender;

    @InjectMocks
    private TrainingServiceImpl trainingServiceImpl;

    @Test
    void createGivenTrainingNullThenException() {
        Training training = null;

        var e = assertThrows(NullPointerException.class, () -> trainingServiceImpl.create(training));
        assertEquals("Training can't be null", e.getMessage());
    }

    @Test
    void createGivenTrainingTraineeNullThenException() {
        Training training = Training.builder().build();

        var e = assertThrows(NullPointerException.class, () -> trainingServiceImpl.create(training));
        assertEquals("Training.trainee can't be null", e.getMessage());
    }

    @Test
    void createGivenTrainingTrainerNullThenException() {
        Training training = Training.builder()
                .trainee(new Trainee())
                .build();

        var e = assertThrows(NullPointerException.class, () -> trainingServiceImpl.create(training));
        assertEquals("Training.trainer can't be null", e.getMessage());
    }

    @Test
    void createGivenTrainingNameNullThenException() {
        Training training = Training.builder()
                .trainee(new Trainee())
                .trainer(new Trainer())
                .build();

        var e = assertThrows(NullPointerException.class, () -> trainingServiceImpl.create(training));
        assertEquals("Training.trainingName can't be null", e.getMessage());
    }

    @Test
    void createGivenTrainingTypeNullThenException() {
        Training training = Training.builder()
                .trainee(new Trainee())
                .trainer(new Trainer())
                .trainingName("TRAINING NAME")
                .build();

        var e = assertThrows(NullPointerException.class, () -> trainingServiceImpl.create(training));
        assertEquals("Training.trainingType can't be null", e.getMessage());
    }

    @Test
    void createGivenTrainingDateNullThenException() {
        Training training = Training.builder()
                .trainee(new Trainee())
                .trainer(new Trainer())
                .trainingName("TRAINING NAME")
                .trainingType(new TrainingType())
                .build();

        var e = assertThrows(NullPointerException.class, () -> trainingServiceImpl.create(training));
        assertEquals("Training.trainingDate can't be null", e.getMessage());
    }

    @Test
    void createGivenTrainingDurationNullThenException() {
        Training training = Training.builder()
                .trainee(new Trainee())
                .trainer(new Trainer())
                .trainingName("TRAINING NAME")
                .trainingType(new TrainingType())
                .trainingDate(LocalDate.MIN)
                .build();

        var e = assertThrows(NullPointerException.class, () -> trainingServiceImpl.create(training));
        assertEquals("Training.trainingDuration can't be null", e.getMessage());
    }

    @Test
    void createGivenTrainingThenSuccess() throws JsonProcessingException {
        Training training = Training.builder()
                .trainee(new Trainee())
                .trainer(new Trainer())
                .trainingName("TRAINING NAME")
                .trainingType(new TrainingType())
                .trainingDate(LocalDate.MIN)
                .trainingDuration(10L)
                .build();

        when(trainingDao.create(any())).thenReturn(training);
        when(trainerMapper.trainerToTrainerWorkload(any(), any(), any())).thenReturn(new TrainerWorkload());
        doThrow(JsonProcessingException.class).when(messageSender).sendMessageToTrainerWorkload(any());

        assertEquals(training, trainingServiceImpl.create(training));
        verify(trainingDao, times(1)).create(any());
        verify(trainerMapper, times(1)).trainerToTrainerWorkload(any(), any(), any());
        verify(messageSender, times(1)).sendMessageToTrainerWorkload(any());
    }

    @Test
    void getByIdGivenTrainingThenSuccess() {
        Training training = new Training();

        when(trainingDao.getById(any())).thenReturn(Optional.of(training));

        Assertions.assertEquals(training, trainingServiceImpl.getById(1L));
        verify(trainingDao, times(1)).getById(any());
    }

    @Test
    void getByIdGivenNotFoundTrainingThenFail() {
        Training training = new Training();

        when(trainingDao.getById(any())).thenReturn(Optional.empty());

        var e = assertThrows(NoSuchElementException.class, () -> trainingServiceImpl.getById(training.getId()));

        assertEquals("Training not found with id = " + training.getId(), e.getMessage());
        verify(trainingDao, times(1)).getById(any());
    }

    @Test
    void getAllGivenValidThenSuccess() {
        when(trainingDao.getAll()).thenReturn(List.of(new Training(), new Training()));

        List<Training> trainings = trainingServiceImpl.getAll();

        assertNotNull(trainings);
        assertEquals(2, trainings.size());
        verify(trainingDao, times(1)).getAll();
    }

    @Test
    void getTrainingsByCriteriaTraineeThenSuccess() {
        when(trainingDao.getTrainingsByCriteria(any(TraineeTrainingsRequest.class)))
                .thenReturn(List.of(new Training(), new Training()));

        assertEquals(2, trainingServiceImpl.getTrainingsByCriteria(new TraineeTrainingsRequest()).size());
    }

    @Test
    void getTrainingsByCriteriaTrainerThenSuccess() {
        when(trainingDao.getTrainingsByCriteria(any(TrainerTrainingRequest.class)))
                .thenReturn(List.of(new Training(), new Training()));

        assertEquals(2, trainingServiceImpl.getTrainingsByCriteria(new TrainerTrainingRequest()).size());
    }

    @Test
    void deleteTrainingByIdThenSuccess() throws JsonProcessingException {
        when(trainingDao.getById(any())).thenReturn(Optional.of(new Training()));
        when(trainerMapper.trainerToTrainerWorkload(any(), any(), any())).thenReturn(new TrainerWorkload());

        trainingServiceImpl.deleteTrainingById(1L);

        verify(trainingDao, times(1)).getById(any());
        verify(trainerMapper, times(1)).trainerToTrainerWorkload(any(), any(), any());
        verify(messageSender, times(1)).sendMessageToTrainerWorkload(any());
    }

    @Test
    void deleteTrainingByIdThenSuccessAndJsonProcessingExceptionThrownInMessageSender() throws JsonProcessingException {
        when(trainingDao.getById(any())).thenReturn(Optional.of(new Training()));
        when(trainerMapper.trainerToTrainerWorkload(any(), any(), any())).thenReturn(new TrainerWorkload());
        doThrow(JsonProcessingException.class).when(messageSender).sendMessageToTrainerWorkload(any());

        trainingServiceImpl.deleteTrainingById(1L);

        verify(trainingDao, times(1)).getById(any());
        verify(trainerMapper, times(1)).trainerToTrainerWorkload(any(), any(), any());
        verify(messageSender, times(1)).sendMessageToTrainerWorkload(any());
    }
}
