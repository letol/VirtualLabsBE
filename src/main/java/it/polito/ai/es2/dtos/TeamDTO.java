package it.polito.ai.es2.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TeamDTO {

    private Long id;

    @NonNull
    private String name;

    private int status = 0;
}
