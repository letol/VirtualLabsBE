package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.VmIstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VmIstanceRepository extends JpaRepository<VmIstance, Long> {
}
