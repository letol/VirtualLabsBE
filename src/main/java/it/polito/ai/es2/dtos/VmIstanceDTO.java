package it.polito.ai.es2.dtos;

import it.polito.ai.es2.VmStatus;
import lombok.*;

import javax.persistence.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmIstanceDTO {

    private Long id;
    private int vcpu;
    private Float disk;
    private Float memory;
    private VmStatus status;

}
