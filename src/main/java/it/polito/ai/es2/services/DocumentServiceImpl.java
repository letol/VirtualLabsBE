package it.polito.ai.es2.services;

import it.polito.ai.es2.entities.Document;
import it.polito.ai.es2.properties.DocumentStorageProperty;
import it.polito.ai.es2.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepo;

    private final Path docStorageLocation;

    @Autowired
    public DocumentServiceImpl(DocumentStorageProperty documentStorageProperty) throws IOException {
        this.docStorageLocation = Paths.get(documentStorageProperty.getUploadDirectory())
                .toAbsolutePath().normalize();
        Files.createDirectories(this.docStorageLocation);
    }

    @Override
    public Document addDocument(MultipartFile multipartFile) throws NoSuchAlgorithmException, IOException {
        Document document = new Document();
        document.setName(multipartFile.getOriginalFilename());
        document.setMimeType(multipartFile.getContentType());
        document.setSize(multipartFile.getSize());
        document.setHash();
        storeDocument(multipartFile, document.getHash());
        documentRepo.save(document);
        return document;
    }

    @Override
    public void removeDocument(Document document) throws IOException {
        deleteDocument(document.getHash());
    }

    @Override
    public ByteArrayResource getDocumentContent(Document document) throws IOException {
        return new ByteArrayResource(readDocument(document.getHash()));
    }

    private void storeDocument(MultipartFile file, String hash) throws IOException {
        Path targetLocation = this.docStorageLocation.resolve(hash);
        Files.copy(file.getInputStream(), targetLocation);
    }

    private void deleteDocument(String hash) throws IOException {
        Path targetLocation = this.docStorageLocation.resolve(hash);
        Files.delete(targetLocation);
    }

    private byte[] readDocument(String hash) throws IOException {
        Path targetLocation = this.docStorageLocation.resolve(hash);
        return Files.readAllBytes(targetLocation);
    }
}
