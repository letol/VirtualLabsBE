package it.polito.ai.es2.dtos;

import it.polito.ai.es2.utility.VmStatus;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmInstanceDTO {

    private Long id;

    @NotEmpty
    private int vcpu;

    @NotEmpty
    private Float disk;

    @NotEmpty
    private Float memory;

    @NotEmpty
    private VmStatus status;

}
