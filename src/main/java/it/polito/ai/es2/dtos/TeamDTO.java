package it.polito.ai.es2.dtos;

import it.polito.ai.es2.utility.TeamStatus;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {

    private Long id;

    @NotBlank
    private String name;

    private TeamStatus status = TeamStatus.PENDING;

    private int vcpuMAX;

    private Float memoryMAX;

    private Float diskMAX;

    private int maxVmIstance;

    private int runningVmIstance;
}
