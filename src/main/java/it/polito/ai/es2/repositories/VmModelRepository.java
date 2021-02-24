package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.VmModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VmModelRepository extends JpaRepository<VmModel, Long> {
}
