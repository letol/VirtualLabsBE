package it.polito.ai.es2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Teacher {

    @Id
    private String id;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 1000)
    private byte[] avatar;

    @OneToOne
    @JoinColumn(name = "auth_user_id")
    private User authUser;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "teacher_course",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_name"))
    private List<Course> courses = new ArrayList<>();

    public boolean addCourse(Course course) {
        if (this.courses.contains(course))
            return false;
        else {
            this.courses.add(course);
            course.getTeachers().add(this);
            return true;
        }
    }
}
