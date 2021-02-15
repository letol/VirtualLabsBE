package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.ProposalNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalNotificationRepository extends JpaRepository <ProposalNotification, Long> {

    @Query("SELECT p FROM ProposalNotification p JOIN p.studentsInvitedWithStatus sp WHERE sp.studentId=:studentId and p.course.id=:courseId")
    List<ProposalNotification> getProposalNotificationsForStudentByCourse(Long courseId, String studentId);

    List<ProposalNotification> getProposalNotificationsByCreator_idAndCourse_Id(String studentId, Long courseId);

}
