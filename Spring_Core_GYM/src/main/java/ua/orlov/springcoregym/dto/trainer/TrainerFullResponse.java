package ua.orlov.springcoregym.dto.trainer;

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

    private Boolean isActive;

    private List<TraineeNamesResponse> trainees;
}
