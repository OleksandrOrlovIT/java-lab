package ua.orlov.springcoregym.dto.trainer;

import lombok.*;
import ua.orlov.springcoregym.model.ActionType;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkload {

    private String trainerUsername;

    private String trainerFirstName;

    private String trainerLastName;

    private boolean trainerIsActive;

    private LocalDate trainingDate;

    private Integer trainingDurationMinutes;

    private ActionType actionType;

}
