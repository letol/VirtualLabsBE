package it.polito.ai.es2.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.ByteArrayResource;

@Data
@Builder
public class DocumentDTO {

    private Long id;

    private String name;

    private String mimeType;

    private long size;

    private ByteArrayResource content;
}
