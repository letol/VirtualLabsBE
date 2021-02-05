package it.polito.ai.es2.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "document")
@Data
public class DocumentStorageProperty {

    private String uploadDirectory;

}
