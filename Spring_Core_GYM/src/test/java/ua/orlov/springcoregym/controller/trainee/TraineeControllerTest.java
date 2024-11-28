package ua.orlov.springcoregym.controller.trainee;

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
import ua.orlov.springcoregym.dto.trainee.*;
import ua.orlov.springcoregym.dto.user.UsernameIsActiveUser;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.dto.user.UsernameUser;
import ua.orlov.springcoregym.mapper.trainee.TraineeMapper;
import ua.orlov.springcoregym.mapper.traineetrainer.TraineeTrainerMapper;
import ua.orlov.springcoregym.mapper.trainer.TrainerMapper;
import ua.orlov.springcoregym.mapper.user.UserMapper;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.service.user.trainee.TraineeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TraineeTrainerMapper traineeTrainerMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TraineeController traineeController;

    private MockMvc mockMvc;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setObjectMapper(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();
    }

    @Test
    void registerTraineeThenSuccess() throws Exception {
        TraineeRegister traineeRegister = new TraineeRegister();
        traineeRegister.setFirstName("first");
        traineeRegister.setLastName("last");
        traineeRegister.setAddress("address");
        traineeRegister.setDateOfBirth(LocalDate.of(2020, 10, 10));

        Trainee returnTrainee = new Trainee();
        UsernamePasswordUser response = new UsernamePasswordUser();

        when(traineeMapper.traineeRegisterToTrainee(any())).thenReturn(returnTrainee);
        when(traineeService.create(any())).thenReturn(returnTrainee);
        when(traineeMapper.traineeToUsernamePasswordUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/trainee/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeRegister)))
                .andExpect(status().isOk())
                .andReturn();

        verify(traineeMapper, times(1)).traineeRegisterToTrainee(any());
        verify(traineeService, times(1)).create(any());
        verify(traineeMapper, times(1)).traineeToUsernamePasswordUser(any());
    }

    @Test
    void getTraineeByUsernameThenSuccess() throws Exception {
        UsernameUser usernameUser = new UsernameUser("username");

        when(traineeService.getByUserNameWithTrainers(any())).thenReturn(new Trainee());
        when(traineeTrainerMapper.traineeToTraineeFullResponse(any())).thenReturn(new TraineeFullResponse());

        mockMvc.perform(post("/api/v1/trainee/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usernameUser)))
                .andExpect(status().isOk())
                .andReturn();

        verify(traineeService, times(1)).getByUserNameWithTrainers(any());
        verify(traineeTrainerMapper, times(1)).traineeToTraineeFullResponse(any());
    }

    @Test
    void updateTraineeThenSuccess() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("username");
        request.setFirstName("firstname");
        request.setLastName("lastname");
        request.setAddress("address");
        request.setDateOfBirth(LocalDate.of(2020, 10, 10));
        request.setIsActive(true);

        when(traineeMapper.updateTraineeRequestToTrainee(any())).thenReturn(new Trainee());
        when(traineeService.update(any())).thenReturn(new Trainee());
        when(traineeTrainerMapper.traineeToTraineeFullUsernameResponse(any())).thenReturn(new TraineeFullUsernameResponse());

        mockMvc.perform(put("/api/v1/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(traineeMapper, times(1)).updateTraineeRequestToTrainee(any());
        verify(traineeService, times(1)).update(any());
        verify(traineeTrainerMapper, times(1)).traineeToTraineeFullUsernameResponse(any());
    }

    @Test
    void deleteTraineeByUsernameThenSuccess() throws Exception {
        UsernameUser usernameUser = new UsernameUser("username");

        mockMvc.perform(delete("/api/v1/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usernameUser)))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(traineeService, times(1)).deleteByUsername(any());
    }

    @Test
    void updateTraineeTrainersListThenSuccess() throws Exception {
        UpdateTraineeTrainersListRequest request = new UpdateTraineeTrainersListRequest();
        request.setUsername("userName");
        request.setTrainers(List.of(new UsernameUser()));

        when(userMapper.mapUsernameUserListToStringList(anyList())).thenReturn(new ArrayList<>());
        when(traineeService.updateTraineeTrainers(any(String.class), anyList())).thenReturn(new ArrayList<>());
        when(trainerMapper.trainersListToTrainerResponseList(anyList())).thenReturn(new ArrayList<>());

        mockMvc.perform(put("/api/v1/trainee/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(userMapper, times(1)).mapUsernameUserListToStringList(any());
        verify(traineeService, times(1)).updateTraineeTrainers(any(String.class), anyList());
        verify(trainerMapper, times(1)).trainersListToTrainerResponseList(any());
    }

    @Test
    void activateDeactivateTraineeByUsernameThenSuccess() throws Exception {
        UsernameIsActiveUser request = new UsernameIsActiveUser();
        request.setUsername("username");
        request.setIsActive(true);

        mockMvc.perform(patch("/api/v1/trainee/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        verify(traineeService, times(1)).activateDeactivateTrainee(any(), any(Boolean.class));
    }
}
