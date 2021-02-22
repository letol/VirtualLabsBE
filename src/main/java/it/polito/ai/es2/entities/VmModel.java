package it.polito.ai.es2.entities;
import javax.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmModel {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String configuration;

    //Todo corretto?
    @OneToOne(mappedBy = "vmModel")
    private Course course;

    @OneToMany(mappedBy = "vmModel", cascade = CascadeType.REMOVE)
    private List<VmInstance> vmInstances;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VmModel vmModel = (VmModel) o;
        return Objects.equals(id, vmModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
