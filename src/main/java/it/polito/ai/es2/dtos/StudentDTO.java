package it.polito.ai.es2.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {

    @CsvBindByName(required = true)
    private String id;

    @CsvBindByName(required = true)
    private String lastName;

    @CsvBindByName(required = true)
    private String firstName;
}
