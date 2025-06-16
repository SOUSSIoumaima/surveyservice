package horizon.surveyservice.service;

import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.DTO.SurveyDto;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    List<QuestionDto> getAllQuestions();
    QuestionDto getQuestionById(UUID id);
    QuestionDto createQuestion(QuestionDto questionDto);
    QuestionDto updateQuestion(UUID id,QuestionDto questionDto);
    List<QuestionDto> getBySubject(String subject);
    void deleteQuestion(UUID id);
    QuestionDto lockQuestion(UUID id);
    QuestionDto unlockQuestion(UUID id);
    List<QuestionDto> getQuestionByOrganization(UUID organizationId);


}
