package it.polito.ai.es2.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmModelDTO {

    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String configuration;
}
