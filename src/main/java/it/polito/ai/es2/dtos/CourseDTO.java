package it.polito.ai.es2.dtos;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CourseDTO extends RepresentationModel<CourseDTO> {

    @NotBlank
    private String name;

    @NonNull
    private Integer min;

    @NonNull
    private Integer max;

    private Boolean enabled = false;
}
