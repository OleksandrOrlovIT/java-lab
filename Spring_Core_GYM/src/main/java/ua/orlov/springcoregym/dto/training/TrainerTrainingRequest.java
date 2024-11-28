package ua.orlov.springcoregym.dto.training;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerTrainingRequest {

    @NotBlank(message = "username is required.")
    private String username;

    private LocalDate startDate;

    private LocalDate endDate;

    private String traineeUsername;
}
