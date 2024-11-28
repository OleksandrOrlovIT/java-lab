package ua.orlov.springcoregym.model.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pageable {
    private int pageNumber;
    private int pageSize;
}
