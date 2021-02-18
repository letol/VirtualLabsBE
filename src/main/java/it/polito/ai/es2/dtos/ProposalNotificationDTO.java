package it.polito.ai.es2.dtos;

import it.polito.ai.es2.utility.StudentStatusInvitation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposalNotificationDTO extends RepresentationModel<ProposalNotificationDTO> {

    private Long id;

    @NotBlank
    private String teamName;

    private String token;

    @NotBlank
    private Timestamp deadline;

    @NotBlank
    private List<StudentStatusInvitation> studentsInvitedWithStatus;
}