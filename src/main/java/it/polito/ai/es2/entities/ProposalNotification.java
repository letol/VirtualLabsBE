package it.polito.ai.es2.entities;

import lombok.*;

import javax.persistence.*;
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

    @NonNull
    @Column(nullable = false)
    String token;

    @NonNull
    @Column(nullable = false)
    String deadline;

    @NonNull
    @Column(nullable = false)
    boolean isValid;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "course_id")
    Student creator;

    @ManyToMany(mappedBy = "notifications")
    List<Student> members;

}
