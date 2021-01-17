package it.polito.ai.es2.entities;
import javax.persistence.*;

import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmIstance {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(nullable = false)
    private int vcpu;

    @NonNull
    @Column(nullable = false)
    private float disk;

    @NonNull
    @Column(nullable = false)
    private float memory;

    @ManyToOne()
    @JoinColumn(name = "vmModel_id", nullable = false)
    private VmModel vmModel;

    @ManyToMany(mappedBy = "vmIstances")
    private List<Student> owners;

    @ManyToOne()
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public boolean addOwner(Student student) {
        if (this.owners.contains(student))
            return false;
        else {
            this.owners.add(student);
            student.getOwnedVMs().add(this);
            return true;
        }
    }


}

