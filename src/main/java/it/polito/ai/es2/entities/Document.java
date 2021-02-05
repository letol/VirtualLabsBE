package it.polito.ai.es2.entities;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Entity
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String mimeType;

    private long size;

    @Column(nullable = false, unique = true)
    private String hash;

    public static final int RADIX = 16;

    public void setHash() throws NoSuchAlgorithmException {
        String transformedName = this.name +
                this.mimeType +
                this.size +
                new Date().getTime();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(transformedName.getBytes(StandardCharsets.UTF_8));
        this.hash = new BigInteger(1, messageDigest.digest()).toString(RADIX);
    }
}
