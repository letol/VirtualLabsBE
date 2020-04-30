package it.polito.ai.es2.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO extends RepresentationModel<StudentDTO> {

    @CsvBindByName(required = true)
    private String id;

    @CsvBindByName(required = true)
    private String lastName;

    @CsvBindByName(required = true)
    private String firstName;
}
