package it.polito.ai.es2.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    @NonNull
    private String name;

    @NonNull
    private int min;

    @NonNull
    private int max;

    private boolean enabled;
}
