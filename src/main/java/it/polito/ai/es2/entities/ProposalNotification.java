package it.polito.ai.es2.entities;

import it.polito.ai.es2.utility.StudentStatusInvitation;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalNotification {

    @Id
    @GeneratedValue
    Long id;

    @NonNull
    @Column(nullable = false)
    String teamName;

    @Column()
    String token;

    @NonNull
    @Column(nullable = false)
    Timestamp deadline;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "creator_id")
    Student creator;

    @ElementCollection
    private List<StudentStatusInvitation>  studentsInvitedWithStatus;

}
