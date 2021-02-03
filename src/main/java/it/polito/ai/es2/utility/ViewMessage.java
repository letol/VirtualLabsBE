package it.polito.ai.es2.utility;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewMessage {

    private String title;

    private String message;

    private String borderColour;
}
