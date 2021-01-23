package it.polito.ai.es2.entities;

import it.polito.ai.es2.TeamStatus;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(nullable = false)
    @PrimaryKeyJoinColumn
    private String name;

    private TeamStatus status = TeamStatus.PENDING;

    @NonNull
    @Column(nullable = false)
    private int vcpuMAX;

    @NonNull
    @Column(nullable = false)
    private Float memoryMAX;

    @NonNull
    @Column(nullable = false)
    private Float diskMAX;

    private int maxVmIstance = 0;

    private int runningVmIstance = 0;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @PrimaryKeyJoinColumn
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_team",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> members = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<VmIstance> vmIstances = new ArrayList<>();

    public boolean setCourse(Course course) {
        if (this.course == course)
            return false;

        if (this.course != null)
            this.course.getTeams().remove(this);

        if (course != null && !course.getTeams().contains(this))
            course.getTeams().add(this);

        this.course = course;
        return true;
    }

    public boolean addMember(Student student) {
        if (this.members.contains(student))
            return false;
        else {
            this.members.add(student);
            student.getTeams().add(this);
            return true;
        }
    }

    public boolean removeMember(Student student) {
        if (this.members.contains(student)) {
            this.members.remove(student);
            student.getTeams().remove(this);
            return true;
        } else
            return false;
    }

    public boolean addVmIstanceToTeam(VmIstance vmIstance) {
        int totCpu = vmIstance.getVcpu();
        Float totMemory = vmIstance.getMemory();
        Float totDisk = vmIstance.getDisk();

        for (VmIstance var:
                vmIstances ) {
            totCpu+=var.getVcpu();
            totMemory+=var.getMemory();
            totDisk+=var.getDisk();
        }
        if(totCpu <= vcpuMAX && totMemory <= memoryMAX && totDisk <= diskMAX)
        {
            vmIstances.add(vmIstance);
            maxVmIstance ++;
            return true;
        }
        return false;
    }
    public boolean removeVmIstance(VmIstance vmIstance) {
        int totCpu = vmIstance.getVcpu();
        Float totMemory = vmIstance.getMemory();
        Float totDisk = vmIstance.getDisk();

        for (VmIstance var:
                vmIstances ) {
            totCpu+=var.getVcpu();
            totMemory+=var.getMemory();
            totDisk+=var.getDisk();
        }
        if(totCpu <= vcpuMAX && totMemory <= memoryMAX && totDisk <= diskMAX)
        {
            vmIstances.add(vmIstance);
            maxVmIstance ++;
            return true;
        }
        return false;
    }
}
