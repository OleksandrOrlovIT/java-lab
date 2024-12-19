package ua.orlov.springcoregym.controller.trainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.orlov.springcoregym.dto.trainer.TrainerFullResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerFullUsernameResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerRegister;
import ua.orlov.springcoregym.dto.trainer.UpdateTrainerRequest;
import ua.orlov.springcoregym.dto.user.UsernameIsActiveUser;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.dto.user.UsernameUser;
import ua.orlov.springcoregym.mapper.traineetrainer.TraineeTrainerMapper;
import ua.orlov.springcoregym.mapper.trainer.TrainerMapper;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.service.user.trainer.TrainerService;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TraineeTrainerMapper traineeTrainerMapper;

    @InjectMocks
    private TrainerController trainerController;

    private MockMvc mockMvc;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setObjectMapper(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainerController).build();
    }

    @Test
    void registerTrainerThenSuccess() throws Exception {
        TrainerRegister trainerRegister = new TrainerRegister();
        trainerRegister.setFirstName("first");
        trainerRegister.setLastName("last");
        trainerRegister.setSpecializationId(1L);

        when(trainerService.createFromTrainerRegister(any())).thenReturn(new Trainer());
        when(trainerMapper.trainerToUsernamePasswordUser(any())).thenReturn(new UsernamePasswordUser());

        mockMvc.perform(post("/api/v1/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerRegister)))
                .andExpect(status().isOk())
                .andReturn();

        verify(trainerService, times(1)).createFromTrainerRegister(any());
        verify(trainerMapper, times(1)).trainerToUsernamePasswordUser(any());
    }

    @Test
    void getTrainerByUsernameThenSuccess() throws Exception {
        UsernameUser usernameUser = new UsernameUser("username");

        when(trainerService.getByUserNameWithTrainees(any())).thenReturn(new Trainer());
        when(traineeTrainerMapper.trainerToTrainerFullResponse(any())).thenReturn(new TrainerFullResponse());

        mockMvc.perform(get("/api/v1/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usernameUser)))
                .andExpect(status().isOk())
                .andReturn();

        verify(trainerService, times(1)).getByUserNameWithTrainees(any());
        verify(traineeTrainerMapper, times(1)).trainerToTrainerFullResponse(any());
    }

    @Test
    void updateTrainerThenSuccess() throws Exception {
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("username");
        request.setFirstName("firstname");
        request.setLastName("lastname");
        request.setActive(true);
        request.setSpecializationId(1L);

        when(trainerService.updateFromUpdateTrainerRequest(any())).thenReturn(new Trainer());
        when(traineeTrainerMapper.trainerToTrainerFullUsernameResponse(any())).thenReturn(new TrainerFullUsernameResponse());

        mockMvc.perform(put("/api/v1/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(trainerService, times(1)).updateFromUpdateTrainerRequest(any());
        verify(traineeTrainerMapper, times(1)).trainerToTrainerFullUsernameResponse(any());
    }

    @Test
    void getTrainersWithoutTraineeThenSuccess() throws Exception {
        UsernameUser usernameUser = new UsernameUser("username");

        when(trainerService.getTrainersWithoutPassedTrainee(any(), any())).thenReturn(new ArrayList<>());
        when(trainerMapper.trainersListToTrainerResponseList(anyList())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/trainer/without-trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usernameUser)))
                .andExpect(status().isOk())
                .andReturn();

        verify(trainerService, times(1)).getTrainersWithoutPassedTrainee(any(), any());
        verify(trainerMapper, times(1)).trainersListToTrainerResponseList(any());
    }

    @Test
    void activateDeactivateTrainerByUsernameThenSuccess() throws Exception {
        UsernameIsActiveUser request = new UsernameIsActiveUser();
        request.setUsername("username");
        request.setActive(true);

        mockMvc.perform(patch("/api/v1/trainer/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(trainerService, times(1)).activateDeactivateTrainer(any(), any(Boolean.class));
    }
}
