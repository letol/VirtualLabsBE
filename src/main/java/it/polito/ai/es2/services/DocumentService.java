package it.polito.ai.es2.services;

import it.polito.ai.es2.entities.Document;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface DocumentService {

    Document addDocument(MultipartFile multipartFile) throws NoSuchAlgorithmException, IOException;

    void removeDocument(Document document) throws IOException;

    ByteArrayResource getDocumentContent(Document document) throws IOException;
}
