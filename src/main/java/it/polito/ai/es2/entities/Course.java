package it.polito.ai.es2.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue
    Long id;

    @Column(nullable = false)
    private String acronym;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int min;

    @NonNull
    @Column(nullable = false)
    private int max;

    @NonNull
    private boolean enabled;

    @NonNull
    @Column(nullable = false)
    private int vcpu;

    @NonNull
    @Column(nullable = false)
    private Float disk;

    @NonNull
    @Column(nullable = false)
    private Float memory;

    @NonNull
    @Column(nullable = false)
    private int maxVmIstance;

    @NonNull
    @Column(nullable = false)
    private int maxRunningVmInstance;

    @ManyToOne
    private Teacher teacher;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Team> teams;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vmModel_id", referencedColumnName = "id")
    private VmModel vmModel;

    @OneToMany(mappedBy = "course")
    private List<ProposalNotification> proposalNotifications;

    public boolean addStudent(Student student) {
        if (this.students.contains(student))
            return false;
        else {
            this.students.add(student);
            student.getCourses().add(this);
            return true;
        }
    }

    public boolean addTeam(Team team) {
        if (this.teams.contains(team))
            return false;
        else {
            this.teams.add(team);
            team.setCourse(this);
            return true;
        }
    }

    public boolean removeTeam(Team team) {
        if (this.teams.contains(team)) {
            this.teams.remove(team);
            team.setCourse(null);
            return true;
        } else
            return false;
    }
}
