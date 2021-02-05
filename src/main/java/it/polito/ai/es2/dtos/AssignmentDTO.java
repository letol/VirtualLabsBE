package it.polito.ai.es2.dtos;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AssignmentDTO extends RepresentationModel<AssignmentDTO> {

    private Long id;

    @NotNull
    private String name;

    private Timestamp releaseDate;

    @NotNull
    private Timestamp expiryDate;
}
