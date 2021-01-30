package it.polito.ai.es2.dtos;

import it.polito.ai.es2.entities.Homework;
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

    private Long id;

    @NotNull
    private byte[] content;

    private Timestamp timestamp;

    private Homework.homeworkStatus versionStatus;

}
