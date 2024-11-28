package ua.orlov.springcoregym.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsernameIsActiveUser {

    @NotBlank(message = "username is required.")
    private String username;

    @NotNull(message = "isActive is required.")
    private Boolean isActive;
}
