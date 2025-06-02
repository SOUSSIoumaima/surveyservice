package horizon.surveyservice.service;

import horizon.surveyservice.DTO.OptionResponseDto;
import horizon.surveyservice.DTO.QuestionResponseDto;


import java.util.List;

public interface QuestionResponseService {
    QuestionResponseDto submitQuestionResponse(QuestionResponseDto questionResponseDto);
    QuestionResponseDto updateQuestionResponse(Long id,QuestionResponseDto questionResponseDto);
    QuestionResponseDto getQuestionResponseById(Long id);
    List<QuestionResponseDto> getQuestionResponseBySurveyResponseId(Long surveyResponseId);
    List<QuestionResponseDto> getAllQuestionResponses();
    void deleteQuestionResponse(Long id);
}
