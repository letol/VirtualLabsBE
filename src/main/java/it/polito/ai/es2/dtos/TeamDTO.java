package it.polito.ai.es2.dtos;

import it.polito.ai.es2.TeamStatus;
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
}
