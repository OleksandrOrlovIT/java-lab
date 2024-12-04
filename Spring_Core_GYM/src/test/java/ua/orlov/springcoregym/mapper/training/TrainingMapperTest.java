package ua.orlov.springcoregym.mapper.training;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.springcoregym.dto.training.CreateTrainingRequest;
import ua.orlov.springcoregym.dto.training.TrainingFullResponse;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.training.TrainingType;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.service.training.TrainingTypeService;
import ua.orlov.springcoregym.service.user.trainee.TraineeService;
import ua.orlov.springcoregym.service.user.trainer.TrainerService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingMapperTest {

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingMapper trainingMapper;

    @Test
    void trainingToTrainingFullResponseThenSuccess() {
        Training training = new Training();
        training.setTrainingName("name");
        training.setTrainingDate(LocalDate.MIN);
        training.setTrainingDurationMinutes(50);
        training.setTrainingType(new TrainingType());
        training.setTrainer(Trainer.builder().username("name").build());

        when(trainingTypeMapper.trainingTypeToTrainingTypeResponse(any())).thenReturn(new TrainingTypeResponse());

        TrainingFullResponse response = trainingMapper.trainingToTrainingFullResponse(training);

        assertNotNull(response);
        assertEquals(training.getTrainingName(), response.getTrainingName());
        assertEquals(training.getTrainingDate(), response.getTrainingDate());
        assertEquals(training.getTrainingDurationMinutes(), response.getTrainingDurationMinutes());
        assertNotNull(response.getTrainingType());
        assertEquals(training.getTrainer().getUsername(), response.getTrainerName());

        verify(trainingTypeMapper, times(1)).trainingTypeToTrainingTypeResponse(any());
    }

    @Test
    void trainingListToTrainingFullResponseListThenSuccess() {
        Training training1 = new Training();
        training1.setTrainingName("name");
        training1.setTrainingDate(LocalDate.MIN);
        training1.setTrainingDurationMinutes(50);
        training1.setTrainingType(new TrainingType());
        training1.setTrainer(Trainer.builder().username("name").build());

        Training training2 = new Training();
        training2.setTrainingName("name");
        training2.setTrainingDate(LocalDate.MIN);
        training2.setTrainingDurationMinutes(50);
        training2.setTrainingType(new TrainingType());
        training2.setTrainer(Trainer.builder().username("name").build());

        when(trainingTypeMapper.trainingTypeToTrainingTypeResponse(any())).thenReturn(new TrainingTypeResponse());

        List<TrainingFullResponse> response =
                trainingMapper.trainingListToTrainingFullResponseList(List.of(training1, training2));

        assertNotNull(response);
        assertEquals(training1.getTrainingName(), response.get(0).getTrainingName());
        assertEquals(training1.getTrainingDate(), response.get(0).getTrainingDate());
        assertEquals(training1.getTrainingDurationMinutes(), response.get(0).getTrainingDurationMinutes());
        assertNotNull(response.get(0).getTrainingType());
        assertEquals(training1.getTrainer().getUsername(), response.get(0).getTrainerName());

        assertEquals(training2.getTrainingName(), response.get(1).getTrainingName());
        assertEquals(training2.getTrainingDate(), response.get(1).getTrainingDate());
        assertEquals(training2.getTrainingDurationMinutes(), response.get(1).getTrainingDurationMinutes());
        assertNotNull(response.get(1).getTrainingType());
        assertEquals(training2.getTrainer().getUsername(), response.get(1).getTrainerName());


        verify(trainingTypeMapper, times(2)).trainingTypeToTrainingTypeResponse(any());
    }

    @Test
    void createTrainingRequestToTraining() {
        CreateTrainingRequest request = new CreateTrainingRequest();
        request.setTrainingName("name");
        request.setTrainingDate(LocalDate.MIN);
        request.setTrainingDurationMinutes(50);
        request.setTraineeUsername("Trainee");
        request.setTrainerUsername("Trainer");
        request.setTrainingTypeId(1L);

        when(traineeService.getByUsername(any()))
                .thenReturn(Trainee.builder().username(request.getTraineeUsername()).build());
        when(trainerService.getByUsername(any()))
                .thenReturn(Trainer.builder().username(request.getTrainerUsername()).build());
        when(trainingTypeService.getById(any()))
                .thenReturn(TrainingType.builder().id(request.getTrainingTypeId()).build());

        Training training = trainingMapper.createTrainingRequestToTraining(request);
        assertNotNull(training);
        assertEquals(request.getTrainingName(), training.getTrainingName());
        assertEquals(request.getTrainingDate(), training.getTrainingDate());
        assertEquals(request.getTrainingDurationMinutes(), training.getTrainingDurationMinutes());
        assertEquals(request.getTrainerUsername(), training.getTrainer().getUsername());
        assertEquals(request.getTrainerUsername(), training.getTrainer().getUsername());
        assertEquals(request.getTrainingTypeId(), training.getTrainingType().getId());

        verify(traineeService, times(1)).getByUsername(any());
        verify(trainerService, times(1)).getByUsername(any());
        verify(trainingTypeService, times(1)).getById(any());
    }
}
