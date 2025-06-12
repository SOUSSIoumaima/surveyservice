package horizon.surveyservice.repository;

import horizon.surveyservice.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SurveyRepository extends JpaRepository<Survey, UUID> {
    List<Survey> findByOrganizationId(UUID organizationId);

}
