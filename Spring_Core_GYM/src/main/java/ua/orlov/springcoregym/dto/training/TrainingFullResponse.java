package ua.orlov.springcoregym.dto.training;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingFullResponse {

    private String trainingName;

    private LocalDate trainingDate;

    private TrainingTypeResponse trainingType;

    private Long trainingDuration;

    private String trainerName;
}
