package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.Homework;
import it.polito.ai.es2.entities.HomeworkVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkVersionRepository extends JpaRepository<HomeworkVersion, Long> {
    List<HomeworkVersion> findAllByHomework(Homework homework);
}
