package it.polito.ai.es2.dtos;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO extends RepresentationModel<CourseDTO> {

    @NonNull
    private String name;

    @NonNull
    private int min;

    @NonNull
    private int max;

    private boolean enabled;
}
