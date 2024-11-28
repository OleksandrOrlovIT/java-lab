package ua.orlov.springcoregym.dto.trainingtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingTypeResponse {

    private Long id;

    private String trainingTypeName;
}
