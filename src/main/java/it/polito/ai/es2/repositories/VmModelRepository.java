package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.Student;
import it.polito.ai.es2.entities.VmModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VmModelRepository extends JpaRepository<VmModel, Long> {
}
