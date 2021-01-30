package it.polito.ai.es2.dtos;

import it.polito.ai.es2.HomeworkId;
import it.polito.ai.es2.entities.Homework.homeworkStatus;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HomeworkDTO extends RepresentationModel<HomeworkDTO> {

    private HomeworkId id;

    private homeworkStatus currentStatus;

    private int score;

}
