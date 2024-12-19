package ua.orlov.springcoregym.controller.training;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.orlov.springcoregym.dto.training.CreateTrainingRequest;
import ua.orlov.springcoregym.dto.training.TraineeTrainingsRequest;
import ua.orlov.springcoregym.dto.training.TrainerTrainingRequest;
import ua.orlov.springcoregym.dto.training.TrainingFullResponse;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;
import ua.orlov.springcoregym.dto.user.UsernameUser;
import ua.orlov.springcoregym.mapper.training.TrainingMapper;
import ua.orlov.springcoregym.mapper.trainingtype.TrainingTypeMapper;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.security.annotations.training.TrainingRequestHasLoggedUser;
import ua.orlov.springcoregym.security.annotations.user.IsSelf;
import ua.orlov.springcoregym.security.training.TrainingSecurity;
import ua.orlov.springcoregym.service.training.TrainingService;
import ua.orlov.springcoregym.service.training.TrainingTypeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/training")
@AllArgsConstructor
public class TrainingController {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeMapper trainingTypeMapper;
    private final TrainingService trainingService;
    private final TrainingMapper trainingMapper;
    private final TrainingSecurity trainingSecurity;

    @Operation(summary = "Get all training types", description = "Retrieves a list of all available training types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of training types",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @GetMapping("/types")
    public List<TrainingTypeResponse> getTrainingTypes() {
        return trainingTypeMapper.trainingTypeListToTrainingTypeResponseList(trainingTypeService.getAll());
    }

    @Operation(summary = "Get trainings by trainee and date",
            description = "Retrieve a list of trainings based on the provided trainee's username and optional date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of trainings",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "No trainings found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @PostMapping("/trainee")
    public List<TrainingFullResponse> getTrainingsByTraineeAndDate(@Validated @RequestBody TraineeTrainingsRequest request){
        List<Training> trainings = trainingService.getTrainingsByCriteria(request);

        return trainingMapper.trainingListToTrainingFullResponseList(trainings);
    }

    @Operation(summary = "Get trainings by trainer and date",
            description = "Retrieve a list of trainings based on the provided trainer's username and optional date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of trainings",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "No trainings found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @PostMapping("/trainer")
    public List<TrainingFullResponse> getTrainingsByTrainerAndDate(@Validated @RequestBody TrainerTrainingRequest request){
        List<Training> trainings = trainingService.getTrainingsByCriteria(request);

        return trainingMapper.trainingListToTrainingFullResponseList(trainings);
    }

    @Operation(summary = "Create a new training",
            description = "Creates a new training session for a trainee with a trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @TrainingRequestHasLoggedUser
    @PostMapping
    public ResponseEntity<?> createTraining(@RequestBody @Validated CreateTrainingRequest request){
        trainingService.createFromCreateTrainingRequest(request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete training by id",
            description = "Deletes a training from the system based on their id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted training"),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Training not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @DeleteMapping
    public ResponseEntity<?> deleteTrainingById(@RequestParam Long trainingId) {
        Training training = trainingService.getById(trainingId);

        if(!trainingSecurity.trainingRequestHasLoggedUser(
                training.getTrainee().getUsername(), training.getTrainer().getUsername())) {
            throw new AccessDeniedException("Logged user doesn't have access to the training");
        }

        trainingService.deleteTrainingById(trainingId);

        return ResponseEntity.noContent().build();
    }
}
