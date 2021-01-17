package it.polito.ai.es2.dtos;

import it.polito.ai.es2.entities.Homework.homeworkStatus;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HomeworkDTO extends RepresentationModel<HomeworkDTO> {

    private homeworkStatus status = homeworkStatus.NULL;

    private int score;

}
