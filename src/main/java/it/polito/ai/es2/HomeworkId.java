package it.polito.ai.es2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HomeworkId implements Serializable {
    private Long assignment_id;
    private String student_id;
}

