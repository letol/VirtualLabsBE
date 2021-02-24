package it.polito.ai.es2.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private int maxVmInstance;

    @NonNull
    @Column(nullable = false)
    private int maxRunningVmInstance;

    @ManyToMany(mappedBy = "courses")
    private List<Teacher> teachers = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Team> teams;

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Assignment> assignments;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vmModel_id", referencedColumnName = "id")
    private VmModel vmModel;

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<ProposalNotification> proposalNotifications;

    public boolean addTeacher(Teacher teacher) {
        if (this.teachers.contains(teacher))
            return false;
        else {
            this.teachers.add(teacher);
            teacher.getCourses().add(this);
            return true;
        }
    }

    public boolean removeTeacher(Teacher teacher) {
        if (this.teachers.contains(teacher)) {
            this.teachers.remove(teacher);
            teacher.getCourses().remove(this);
            return true;
        } else
            return false;
    }

    public boolean addStudent(Student student) {
        if (this.students.contains(student))
            return false;
        else {
            this.students.add(student);
            student.getCourses().add(this);
            return true;
        }
    }

    public boolean removeStudent(Student student) {
        if (this.students.contains(student)) {
            this.students.remove(student);
            student.getCourses().remove(this);
            return true;
        } else
            return false;
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

    public boolean addAssignment(Assignment assignment) {
        if (this.assignments.contains(assignment))
            return false;
        else {
            this.assignments.add(assignment);
            assignment.setCourse(this);
            return true;
        }
    }

    public boolean removeAssignment(Assignment assignment) {
        if (this.assignments.contains(assignment)) {
            this.assignments.remove(assignment);
            assignment.setCourse(null);
            return true;
        } else
            return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
