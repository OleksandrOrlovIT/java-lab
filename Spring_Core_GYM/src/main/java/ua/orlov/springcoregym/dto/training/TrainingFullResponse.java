package ua.orlov.springcoregym.dto.training;

import lombok.*;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingFullResponse {

    private Long trainingId;

    private String trainingName;

    private LocalDate trainingDate;

    private TrainingTypeResponse trainingType;

    private Integer trainingDurationMinutes;

    private String trainerName;
}
