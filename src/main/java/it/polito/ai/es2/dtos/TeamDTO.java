package it.polito.ai.es2.dtos;

import it.polito.ai.es2.utility.TeamStatus;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO extends RepresentationModel<TeamDTO>{

    @Min(value = 1, message = "Minimum numbers of students per team must be 1")
    private Long id;

    @NotBlank
    private String name;

    private TeamStatus status = TeamStatus.PENDING;

    @Min(value = 1, message = "Minimum numbers of cpu per team must be 1")
    private int vcpuMAX;

    @Min(value = 1, message = "Minimum numbers of memory per team must be 1")
    private float memoryMAX;

    @Min(value = 1, message = "Minimum numbers of disk per team must be 1")
    private float diskMAX;

    @Min(value = 1, message = "Minimum numbers of maximum vm instances per team must be 1")
    private int maxVmInstance;

    @Min(value = 1, message = "Minimum numbers of running vm instances per team must be 1")
    private int runningVmInstance;
}
