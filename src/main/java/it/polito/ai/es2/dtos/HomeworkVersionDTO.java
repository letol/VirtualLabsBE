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
public class HomeworkVersionDTO extends RepresentationModel<HomeworkVersionDTO> {

    @NotNull
    private byte[] content;

    private Timestamp timestamp;

}
