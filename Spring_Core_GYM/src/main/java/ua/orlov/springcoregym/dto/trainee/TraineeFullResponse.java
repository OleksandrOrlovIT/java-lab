package ua.orlov.springcoregym.dto.trainee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.orlov.springcoregym.dto.trainer.TrainerResponse;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TraineeFullResponse {

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    @JsonProperty("isActive")
    private boolean isActive;

    private List<TrainerResponse> trainers;
}
