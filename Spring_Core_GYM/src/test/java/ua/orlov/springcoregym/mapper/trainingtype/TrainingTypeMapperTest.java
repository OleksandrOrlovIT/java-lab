package ua.orlov.springcoregym.mapper.trainingtype;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;
import ua.orlov.springcoregym.model.training.TrainingType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeMapperTest {

    @InjectMocks
    private TrainingTypeMapper trainingTypeMapper;

    @Test
    void trainingTypeToTrainingTypeResponseThenSuccess() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("name");

        TrainingTypeResponse response = trainingTypeMapper.trainingTypeToTrainingTypeResponse(trainingType);

        assertNotNull(response);
        assertEquals(trainingType.getId(), response.getId());
        assertEquals(trainingType.getTrainingTypeName(), response.getTrainingTypeName());
    }

    @Test
    void trainingTypeListToTrainingTypeResponseListThenSuccess() {
        TrainingType trainingType1 = new TrainingType();
        trainingType1.setId(1L);
        trainingType1.setTrainingTypeName("name1");

        TrainingType trainingType2 = new TrainingType();
        trainingType2.setId(2L);
        trainingType2.setTrainingTypeName("name2");

        List<TrainingTypeResponse> response =
                trainingTypeMapper.trainingTypeListToTrainingTypeResponseList(List.of(trainingType1, trainingType2));

        assertNotNull(response);
        assertEquals(trainingType1.getId(), response.get(0).getId());
        assertEquals(trainingType1.getTrainingTypeName(), response.get(0).getTrainingTypeName());

        assertEquals(trainingType2.getId(), response.get(1).getId());
        assertEquals(trainingType2.getTrainingTypeName(), response.get(1).getTrainingTypeName());
    }
}
