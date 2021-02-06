package it.polito.ai.es2.dtos;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CourseDTO extends RepresentationModel<CourseDTO> {


    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String acronym;

    @Min(value = 1, message = "Minimum numbers of students per team must be 1")
    private int min;

    @Min(value = 1, message = "Maximum number of students per team not valid")
    private int max;

    @Min(value = 1, message = "Number of vcpu not valid")
    private int vcpu;

    @Min(value = 1, message = "Number of disk not valid")
    private float disk;

    @Min(value = 1, message = "Number of memory not valid")
    private float memory;

    @Min(value = 1, message = "Number of maximum vm instances is not valid")
    private int maxVmInstance;

    @Min(value = 1, message = "Number of maximum running vm instances is not valid")
    private int maxRunningVmInstance;


    private Boolean enabled = false;
}
