package horizon.surveyservice.repository;

import horizon.surveyservice.entity.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionResponseRepository extends JpaRepository<QuestionResponse, Long> {
    List<QuestionResponse> findBySurveyResponseSurveyResponseId(Long surveyResponseId);

}
