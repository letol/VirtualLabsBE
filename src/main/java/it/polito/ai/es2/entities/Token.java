package it.polito.ai.es2.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    String id;

    @Column(nullable = false)
    Long teamId;

    @Column(nullable = false)
    Timestamp expiryDate;
}
