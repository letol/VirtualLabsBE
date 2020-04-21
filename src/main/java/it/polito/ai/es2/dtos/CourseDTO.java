package it.polito.ai.es2.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CourseDTO {

    @NonNull
    private String name;

    @NonNull
    private int min;

    @NonNull
    private int max;

    private boolean enabled;
}
