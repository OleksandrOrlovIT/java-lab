package ua.orlov.springcoregym.mapper.trainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.springcoregym.dto.trainer.TrainerRegister;
import ua.orlov.springcoregym.dto.trainer.TrainerResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerWorkload;
import ua.orlov.springcoregym.dto.trainer.UpdateTrainerRequest;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.exception.BusinessLogicException;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.training.TrainingType;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.service.training.TrainingTypeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerMapperTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TrainerMapper trainerMapper;

    @Test
    void trainerRegisterToTrainer() {
        TrainerRegister trainerRegister = new TrainerRegister();
        trainerRegister.setFirstName("firstname");
        trainerRegister.setLastName("lastname");
        trainerRegister.setSpecializationId(1L);

        when(trainingTypeService.getById(anyLong())).thenReturn(new TrainingType());

        Trainer trainer = trainerMapper.trainerRegisterToTrainer(trainerRegister);

        assertNotNull(trainer);
        assertEquals(trainerRegister.getFirstName(), trainer.getFirstName());
        assertEquals(trainerRegister.getLastName(), trainer.getLastName());
        assertNotNull(trainer.getSpecialization());

        verify(trainingTypeService, times(1)).getById(any());
    }

    @Test
    void trainerToUsernamePasswordUserThenSuccess() {
        Trainer trainer = new Trainer();
        trainer.setUsername("userName");
        trainer.setPassword("password");

        UsernamePasswordUser user = trainerMapper.trainerToUsernamePasswordUser(trainer);

        assertNotNull(user);
        assertEquals(trainer.getUsername(), user.getUsername());
        assertEquals(trainer.getPassword(), user.getPassword());
    }

    @Test
    void trainerToTrainerResponseThenSuccess() {
        Trainer trainer = new Trainer();
        trainer.setUsername("Username");
        trainer.setFirstName("firstname");
        trainer.setLastName("lastname");
        trainer.setSpecialization(new TrainingType());

        when(trainingTypeMapper.trainingTypeToTrainingTypeResponse(any())).thenReturn(new TrainingTypeResponse());

        TrainerResponse response = trainerMapper.trainerToTrainerResponse(trainer);

        assertNotNull(response);
        assertEquals(trainer.getUsername(), response.getUsername());
        assertEquals(trainer.getFirstName(), response.getFirstName());
        assertEquals(trainer.getLastName(), response.getLastName());
        assertNotNull(response.getSpecialization());

        verify(trainingTypeMapper, times(1)).trainingTypeToTrainingTypeResponse(any());
    }

    @Test
    void trainersListToTrainerResponseListThenSuccess() {
        Trainer trainer1 = new Trainer();
        trainer1.setUsername("Username1");
        trainer1.setFirstName("firstname1");
        trainer1.setLastName("lastname1");
        trainer1.setSpecialization(new TrainingType());

        Trainer trainer2 = new Trainer();
        trainer2.setUsername("Username2");
        trainer2.setFirstName("firstname2");
        trainer2.setLastName("lastname2");
        trainer2.setSpecialization(new TrainingType());

        when(trainingTypeMapper.trainingTypeToTrainingTypeResponse(any())).thenReturn(new TrainingTypeResponse());

        List<TrainerResponse> response = trainerMapper.trainersListToTrainerResponseList(List.of(trainer1, trainer2));

        assertNotNull(response);
        assertEquals(trainer1.getUsername(), response.get(0).getUsername());
        assertEquals(trainer1.getFirstName(), response.get(0).getFirstName());
        assertEquals(trainer1.getLastName(), response.get(0).getLastName());
        assertNotNull(response.get(0).getSpecialization());

        assertEquals(trainer2.getUsername(), response.get(1).getUsername());
        assertEquals(trainer2.getFirstName(), response.get(1).getFirstName());
        assertEquals(trainer2.getLastName(), response.get(1).getLastName());
        assertNotNull(response.get(1).getSpecialization());

        verify(trainingTypeMapper, times(2)).trainingTypeToTrainingTypeResponse(any());
    }

    @Test
    void updateTrainerRequestToTrainerThenSuccess() {
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("userName");
        request.setFirstName("firstName");
        request.setLastName("lastName");
        request.setActive(true);
        request.setSpecializationId(1L);

        when(trainingTypeService.getById(anyLong())).thenReturn(new TrainingType());

        Trainer trainer = trainerMapper.updateTrainerRequestToTrainer(request);
        assertNotNull(trainer);
        assertEquals(request.getUsername(), trainer.getUsername());
        assertEquals(request.getFirstName(), trainer.getFirstName());
        assertEquals(request.getLastName(), trainer.getLastName());
        assertEquals(request.isActive(), trainer.isActive());
        assertNotNull(trainer.getSpecialization());

        verify(trainingTypeService, times(1)).getById(anyLong());
    }

    @Test
    void trainerWorkloadToJsonThenSuccess() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenReturn("");

        assertNotNull(trainerMapper.trainerWorkloadToJson(new TrainerWorkload()));

        verify(objectMapper).writeValueAsString(any());
    }

    @Test
    void trainerWorkloadToJsonThenException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException(""){});

        BusinessLogicException e = assertThrows(BusinessLogicException.class,
                () -> trainerMapper.trainerWorkloadToJson(new TrainerWorkload()));

        assertEquals("Serialization error", e.getMessage());
        verify(objectMapper).writeValueAsString(any());
    }
}
