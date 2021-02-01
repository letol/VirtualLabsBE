package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.ProposalNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalNotificationRepository extends JpaRepository <ProposalNotification, Long> {
}
