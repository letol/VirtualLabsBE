package it.polito.ai.es2.utility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
