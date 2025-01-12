package ua.orlov.springcoregym.dto.trainer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.orlov.springcoregym.dto.trainee.TraineeNamesResponse;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerFullResponse {

    private String firstName;

    private String lastName;

    private TrainingTypeResponse specialization;

    @JsonProperty("active")
    private boolean active;

    private List<TraineeNamesResponse> trainees;
}
