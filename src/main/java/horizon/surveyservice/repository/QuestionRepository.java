package horizon.surveyservice.repository;

import horizon.surveyservice.entity.Question;
import horizon.surveyservice.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findBySubject(String subject);
    List<Question> findByOrganizationId(UUID organizationId);


}
