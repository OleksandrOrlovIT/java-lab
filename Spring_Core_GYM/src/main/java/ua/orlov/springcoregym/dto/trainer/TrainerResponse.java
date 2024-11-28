package ua.orlov.springcoregym.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerResponse {

    private String username;

    private String firstName;

    private String lastName;

    private TrainingTypeResponse specialization;
}
