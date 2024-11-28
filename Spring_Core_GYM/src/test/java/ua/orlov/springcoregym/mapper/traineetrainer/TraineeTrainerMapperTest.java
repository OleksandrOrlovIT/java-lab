package ua.orlov.springcoregym.mapper.traineetrainer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.springcoregym.dto.trainee.TraineeFullResponse;
import ua.orlov.springcoregym.dto.trainee.TraineeFullUsernameResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerFullResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerFullUsernameResponse;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;
import ua.orlov.springcoregym.mapper.trainee.TraineeMapper;
import ua.orlov.springcoregym.mapper.trainer.TrainerMapper;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.training.TrainingType;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeTrainerMapperTest {

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @InjectMocks
    private TraineeTrainerMapper traineeTrainerMapper;

    @Test
    void traineeToTraineeFullResponseThenSuccess() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("firstName");
        trainee.setLastName("lastName");
        trainee.setDateOfBirth(LocalDate.MAX);
        trainee.setAddress("address");
        trainee.setActive(true);
        trainee.setTrainers(new ArrayList<>());

        when(trainerMapper.trainersListToTrainerResponseList(anyList())).thenReturn(new ArrayList<>());

        TraineeFullResponse traineeFullResponse = traineeTrainerMapper.traineeToTraineeFullResponse(trainee);

        assertNotNull(traineeFullResponse);
        assertEquals(trainee.getFirstName(), traineeFullResponse.getFirstName());
        assertEquals(trainee.getLastName(), traineeFullResponse.getLastName());
        assertEquals(trainee.getDateOfBirth(), traineeFullResponse.getDateOfBirth());
        assertEquals(trainee.getAddress(), traineeFullResponse.getAddress());
        assertEquals(trainee.isActive(), traineeFullResponse.isActive());
        assertEquals(trainee.getTrainers().size(), traineeFullResponse.getTrainers().size());
        verify(trainerMapper, times(1)).trainersListToTrainerResponseList(anyList());
    }

    @Test
    void traineeToTraineeFullUsernameResponseThenSuccess() {
        Trainee trainee = new Trainee();
        trainee.setUsername("username");
        trainee.setFirstName("firstName");
        trainee.setLastName("lastName");
        trainee.setDateOfBirth(LocalDate.MAX);
        trainee.setAddress("address");
        trainee.setActive(true);
        trainee.setTrainers(new ArrayList<>());

        when(trainerMapper.trainersListToTrainerResponseList(anyList())).thenReturn(new ArrayList<>());

        TraineeFullUsernameResponse traineeFullResponse =
                traineeTrainerMapper.traineeToTraineeFullUsernameResponse(trainee);

        assertNotNull(traineeFullResponse);
        assertEquals(trainee.getUsername(), traineeFullResponse.getUsername());
        assertEquals(trainee.getFirstName(), traineeFullResponse.getFirstName());
        assertEquals(trainee.getLastName(), traineeFullResponse.getLastName());
        assertEquals(trainee.getDateOfBirth(), traineeFullResponse.getDateOfBirth());
        assertEquals(trainee.getAddress(), traineeFullResponse.getAddress());
        assertEquals(trainee.isActive(), traineeFullResponse.isActive());
        assertEquals(trainee.getTrainers().size(), traineeFullResponse.getTrainers().size());
        verify(trainerMapper, times(1)).trainersListToTrainerResponseList(anyList());
    }

    @Test
    void trainerToTrainerFullResponseThenSuccess() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("firstName");
        trainer.setLastName("lastName");
        trainer.setSpecialization(new TrainingType());
        trainer.setActive(true);
        trainer.setTrainees(new ArrayList<>());

        when(trainingTypeMapper.trainingTypeToTrainingTypeResponse(any())).thenReturn(new TrainingTypeResponse());
        when(traineeMapper.traineeListToTraineeNamesResponseList(anyList())).thenReturn(new ArrayList<>());

        TrainerFullResponse trainerFullResponse = traineeTrainerMapper.trainerToTrainerFullResponse(trainer);

        assertNotNull(trainerFullResponse);
        assertEquals(trainer.getFirstName(), trainerFullResponse.getFirstName());
        assertEquals(trainer.getLastName(), trainerFullResponse.getLastName());
        assertEquals(trainer.isActive(), trainerFullResponse.getIsActive());
        assertEquals(trainer.getTrainees().size(), trainerFullResponse.getTrainees().size());
        assertNotNull(trainerFullResponse.getSpecialization());

        verify(trainingTypeMapper, times(1)).trainingTypeToTrainingTypeResponse(any());
        verify(traineeMapper, times(1)).traineeListToTraineeNamesResponseList(anyList());
    }

    @Test
    void trainerToTrainerFullUsernameResponseThenSuccess() {
        Trainer trainer = new Trainer();
        trainer.setUsername("username");
        trainer.setFirstName("firstName");
        trainer.setLastName("lastName");
        trainer.setSpecialization(new TrainingType());
        trainer.setActive(true);
        trainer.setTrainees(new ArrayList<>());

        when(trainingTypeMapper.trainingTypeToTrainingTypeResponse(any())).thenReturn(new TrainingTypeResponse());
        when(traineeMapper.traineeListToTraineeNamesResponseList(anyList())).thenReturn(new ArrayList<>());

        TrainerFullUsernameResponse trainerFullResponse =
                traineeTrainerMapper.trainerToTrainerFullUsernameResponse(trainer);

        assertNotNull(trainerFullResponse);
        assertEquals(trainer.getUsername(), trainerFullResponse.getUsername());
        assertEquals(trainer.getFirstName(), trainerFullResponse.getFirstName());
        assertEquals(trainer.getLastName(), trainerFullResponse.getLastName());
        assertEquals(trainer.isActive(), trainerFullResponse.getIsActive());
        assertEquals(trainer.getTrainees().size(), trainerFullResponse.getTrainees().size());
        assertNotNull(trainerFullResponse.getSpecialization());

        verify(trainingTypeMapper, times(1)).trainingTypeToTrainingTypeResponse(any());
        verify(traineeMapper, times(1)).traineeListToTraineeNamesResponseList(anyList());
    }

}
