package it.polito.ai.es2.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TeacherDTO extends RepresentationModel<TeacherDTO> {

    @NotBlank
    @CsvBindByName(required = true)
    private String id;

    @NotBlank
    @CsvBindByName(required = true)
    private String lastName;

    @NotBlank
    @CsvBindByName(required = true)
    private String firstName;

    @NotBlank
    @CsvBindByName(required = true)
    private String email;

    private byte[] avatar;
}
