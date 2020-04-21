package it.polito.ai.es2.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {

    @CsvBindByName
    private String id;

    @CsvBindByName
    private String lastName;

    @CsvBindByName
    private String firstName;
}
