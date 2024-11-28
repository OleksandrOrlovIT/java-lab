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

    private String username;

    private String firstName;

    private String lastName;

    private Boolean isActive;

    private LocalDate trainingDate;

    private Long trainingDuration;

    private ActionType actionType;

}
