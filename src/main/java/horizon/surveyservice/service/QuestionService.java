package horizon.surveyservice.service;

import horizon.surveyservice.DTO.QuestionDto;

import java.util.List;

public interface QuestionService {
    List<QuestionDto> getAllQuestions();
    QuestionDto getQuestionById(Long id);
    QuestionDto createQuestion(QuestionDto questionDto);
    QuestionDto updateQuestion(Long id,QuestionDto questionDto);
    List<QuestionDto> getBySubject(String subject);
    void deleteQuestion(Long id);
    QuestionDto lockQuestion(Long id);
    QuestionDto unlockQuestion(Long id);

}
