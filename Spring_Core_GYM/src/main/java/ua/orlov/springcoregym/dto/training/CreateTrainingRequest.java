package ua.orlov.springcoregym.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTrainingRequest {

    @NotBlank(message = "traineeUsername is required.")
    private String traineeUsername;

    @NotBlank(message = "trainerUsername is required.")
    private String trainerUsername;

    @NotBlank(message = "trainingName is required.")
    private String trainingName;

    @NotNull(message = "trainingDate is required.")
    private LocalDate trainingDate;

    @NotNull(message = "trainingDuration is required.")
    private Long trainingDuration;

    @NotNull(message = "trainingDuration is required.")
    private Long trainingTypeId;
}
