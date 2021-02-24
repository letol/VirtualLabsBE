package it.polito.ai.es2.dtos;

import it.polito.ai.es2.utility.VmStatus;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmInstanceDTO extends RepresentationModel<VmInstanceDTO> {

    private Long id;

    @NotEmpty
    private String name;

    @Min(value = 1, message = "Minimum numbers of cpu must be 1")
    private int vcpu;

    @Min(value = 1, message = "Minimum numbers of disk must be 1")
    private Float disk;

    @Min(value = 1, message = "Minimum numbers of memory must be 1")
    private Float memory;

    private VmStatus status;

}
