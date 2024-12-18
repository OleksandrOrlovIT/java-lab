package ua.orlov.gymtrainerworkload.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "trainers")
public class Trainer {

    @Id
    private String username;

    private String firstName;

    private String lastName;

    private boolean status;

    private List<YearSummary> years;

}
