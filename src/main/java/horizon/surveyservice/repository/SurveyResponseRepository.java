package horizon.surveyservice.repository;

import horizon.surveyservice.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, UUID> {
}
