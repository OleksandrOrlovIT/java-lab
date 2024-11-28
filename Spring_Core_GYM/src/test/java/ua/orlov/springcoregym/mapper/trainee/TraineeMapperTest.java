package ua.orlov.springcoregym.mapper.trainee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.springcoregym.dto.trainee.TraineeNamesResponse;
import ua.orlov.springcoregym.dto.trainee.TraineeRegister;
import ua.orlov.springcoregym.dto.trainee.UpdateTraineeRequest;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.model.user.Trainee;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TraineeMapperTest {

    private TraineeMapper traineeMapper;

    @BeforeEach
    void setUp() {
        traineeMapper = new TraineeMapper();
    }

    @Test
    void traineeRegisterToTraineeThenSuccess() {
        TraineeRegister traineeRegister = new TraineeRegister();
        traineeRegister.setFirstName("firstName");
        traineeRegister.setLastName("lastName");
        traineeRegister.setAddress("address");
        traineeRegister.setDateOfBirth(LocalDate.MAX);

        Trainee convertedTrainee = traineeMapper.traineeRegisterToTrainee(traineeRegister);

        assertEquals(traineeRegister.getFirstName(), convertedTrainee.getFirstName());
        assertEquals(traineeRegister.getLastName(), convertedTrainee.getLastName());
        assertEquals(traineeRegister.getAddress(), convertedTrainee.getAddress());
        assertEquals(traineeRegister.getDateOfBirth(), convertedTrainee.getDateOfBirth());
    }

    @Test
    void traineeToUsernamePasswordUserThenSuccess() {
        Trainee trainee = new Trainee();
        trainee.setUsername("username");
        trainee.setPassword("password");

        UsernamePasswordUser convertedUser = traineeMapper.traineeToUsernamePasswordUser(trainee);

        assertEquals(trainee.getUsername(), convertedUser.getUsername());
        assertEquals(trainee.getPassword(), convertedUser.getPassword());
    }

    @Test
    void updateTraineeRequestToTraineeThenSuccess() {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("userName");
        request.setFirstName("first");
        request.setLastName("last");
        request.setDateOfBirth(LocalDate.MAX);
        request.setAddress("address");
        request.setIsActive(true);

        Trainee trainee = traineeMapper.updateTraineeRequestToTrainee(request);
        assertEquals(request.getUsername(), trainee.getUsername());
        assertEquals(request.getFirstName(), trainee.getFirstName());
        assertEquals(request.getLastName(), trainee.getLastName());
        assertEquals(request.getDateOfBirth(), trainee.getDateOfBirth());
        assertEquals(request.getAddress(), trainee.getAddress());
        assertEquals(request.getIsActive(), trainee.isActive());
    }

    @Test
    void traineeToTraineeNamesResponse() {
        Trainee trainee = new Trainee();
        trainee.setUsername("userName");
        trainee.setFirstName("firstName");
        trainee.setLastName("lastName");

        TraineeNamesResponse traineeNamesResponse = traineeMapper.traineeToTraineeNamesResponse(trainee);
        assertEquals(trainee.getUsername(), traineeNamesResponse.getUsername());
        assertEquals(trainee.getFirstName(), traineeNamesResponse.getFirstName());
        assertEquals(trainee.getLastName(), traineeNamesResponse.getLastName());
    }

    @Test
    void traineeListToTraineeNamesResponseList() {
        Trainee trainee1 = new Trainee();
        trainee1.setUsername("userName1");
        trainee1.setFirstName("firstName1");
        trainee1.setLastName("lastName1");

        Trainee trainee2 = new Trainee();
        trainee1.setUsername("userName2");
        trainee1.setFirstName("firstName2");
        trainee1.setLastName("lastName2");

        List<TraineeNamesResponse> responses =
                traineeMapper.traineeListToTraineeNamesResponseList(List.of(trainee1, trainee2));

        assertEquals(trainee1.getUsername(), responses.get(0).getUsername());
        assertEquals(trainee1.getFirstName(), responses.get(0).getFirstName());
        assertEquals(trainee1.getLastName(), responses.get(0).getLastName());
        assertEquals(trainee2.getUsername(), responses.get(1).getUsername());
        assertEquals(trainee2.getFirstName(), responses.get(1).getFirstName());
        assertEquals(trainee2.getLastName(), responses.get(1).getLastName());
    }

}
