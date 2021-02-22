package it.polito.ai.es2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Data
public class Student {

    @Id
    private String id;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 100000)
    private byte[] avatar;

    @OneToOne
    private User authUser;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "creator")
    private List<VmInstance> createdVMs = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_vms",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "vmInstance_id"))
    private List<VmInstance> ownedVMs = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Team> teams = new ArrayList<>() ;

    @OneToMany(mappedBy = "creator")
    private List<ProposalNotification> notificationsCreated = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Homework> homeworks = new ArrayList<>();

    public boolean addCourse(Course course) {
        if (this.courses.contains(course))
            return false;
        else {
            this.courses.add(course);
            course.getStudents().add(this);
            return true;
        }
    }

    public boolean removeCourse(Course course) {
        if (this.courses.contains(course)) {
            this.courses.remove(course);
            course.getStudents().remove(this);
            return true;
        } else
            return false;
    }

    public boolean addVmOwnership(VmInstance vmInstance) {
        if (this.ownedVMs.contains(vmInstance))
            return false;
        else {
            this.ownedVMs.add(vmInstance);
            vmInstance.getOwners().add(this);
            return true;
        }
    }

    public boolean isOwner(VmInstance vmInstance) {
        return this.ownedVMs.contains(vmInstance);
    }

    public boolean addTeam(Team team) {
        if (this.teams.contains(team))
            return false;
        else {
            this.teams.add(team);
            team.getMembers().add(this);
            return true;
        }
    }

    public boolean removeTeam(Team team) {
        if (this.teams.contains(team)) {
            this.teams.remove(team);
            team.getMembers().remove(this);
            return true;
        } else
            return false;
    }

    public boolean addHomework(Homework homework) {
        if (this.homeworks.contains(homework))
            return false;
        else {
            this.homeworks.add(homework);
            return true;
        }
    }

    public boolean removeHomework(Homework homework) {
        if (this.homeworks.contains(homework)) {
            this.homeworks.remove(homework);
            return true;
        } else
            return false;
    }

    public boolean removeOwnedVm(VmInstance vmInstance) {
        if (this.ownedVMs.contains(vmInstance)) {

            this.ownedVMs.remove(vmInstance);
            return true;
        } else
            return false;
    }
    public boolean removeCreatedVm(VmInstance vmInstance) {
        if (this.createdVMs.contains(vmInstance)) {
            this.createdVMs.remove(vmInstance);
            return true;
        } else
            return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
