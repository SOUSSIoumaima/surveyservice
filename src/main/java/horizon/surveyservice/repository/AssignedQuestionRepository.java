package horizon.surveyservice.repository;

import horizon.surveyservice.entity.AssignedQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssignedQuestionRepository extends JpaRepository<AssignedQuestion, UUID> {
    List<AssignedQuestion> findBySurvey_SurveyId(UUID surveyId);
    void deleteBySurvey_SurveyIdAndQuestion_QuestionId(UUID surveyId, UUID questionId);
    boolean existsBySurvey_SurveyIdAndQuestion_QuestionId(UUID surveyId, UUID questionId);


}
