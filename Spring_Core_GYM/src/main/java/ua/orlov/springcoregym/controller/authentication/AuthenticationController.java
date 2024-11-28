package ua.orlov.springcoregym.controller.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.orlov.springcoregym.dto.jwt.JwtAuthenticationResponse;
import ua.orlov.springcoregym.dto.user.ChangeLoginDto;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.security.annotations.user.IsSelf;
import ua.orlov.springcoregym.service.security.AuthenticationService;
import ua.orlov.springcoregym.service.user.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "User login", description = "Authenticate a user by username and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Validation failed or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "429", description = "Too Many wrong Requests",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @PostMapping("/session")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody @Validated UsernamePasswordUser userNamePasswordUser) {
        JwtAuthenticationResponse response = new JwtAuthenticationResponse(
                        authenticationService.login(userNamePasswordUser.getUsername(), userNamePasswordUser.getPassword()));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Change user password", description = "Change the password for an existing user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "No body inside the request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Validation failed or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found (e.g., NoSuchElementException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @PutMapping("/password")
    public ResponseEntity<String> changeLogin(@RequestBody @Validated ChangeLoginDto request) {
        if(userService.changeUserPassword(request.getUsername(), request.getOldPassword(), request.getNewPassword())) {
            return ResponseEntity.ok("You successfully changed password");
        }

        return ResponseEntity.badRequest().body("Password hasn't been changed");
    }

    @Operation(summary = "Logout from application", description = "Invalidates token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        authenticationService.logout(token);
        return ResponseEntity.ok("Logged out successfully.");
    }
}
