package it.polito.ai.es2.entities;
import javax.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmModel {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String configuration;

    //Todo corretto?
    @OneToOne(mappedBy = "vmModel")
    private Course course;

    @OneToMany(mappedBy = "vmModel")
    private List<VmIstance> vmIstances;


}
