package ua.orlov.springcoregym.dto.trainee;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UpdateTraineeRequest {

    @NotBlank(message = "username is required.")
    private String username;

    @NotBlank(message = "firstName is required.")
    private String firstName;

    @NotBlank(message = "lastName is required.")
    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    @JsonProperty("active")
    private boolean active;
}
