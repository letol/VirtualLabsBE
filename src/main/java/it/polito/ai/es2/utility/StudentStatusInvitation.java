package it.polito.ai.es2.utility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentStatusInvitation {
    private String studentId;
    private ResponseTypeInvitation accepted;
}
