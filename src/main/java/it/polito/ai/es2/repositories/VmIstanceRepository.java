package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.VmInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VmIstanceRepository extends JpaRepository<VmInstance, Long> {
}
