package horizon.surveyservice.repository;

import horizon.surveyservice.entity.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionResponseRepository extends JpaRepository<QuestionResponse, UUID> {
    List<QuestionResponse> findBySurveyResponseSurveyResponseId(UUID surveyResponseId);

}
