package it.polito.ai.es2.repositories;

import it.polito.ai.es2.HomeworkId;
import it.polito.ai.es2.entities.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, HomeworkId> {
}
