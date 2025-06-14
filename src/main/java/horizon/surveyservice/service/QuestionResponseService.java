package horizon.surveyservice.service;

import horizon.surveyservice.DTO.OptionResponseDto;
import horizon.surveyservice.DTO.QuestionResponseDto;


import java.util.List;
import java.util.UUID;

public interface QuestionResponseService {
    QuestionResponseDto submitQuestionResponse(QuestionResponseDto questionResponseDto);
    QuestionResponseDto updateQuestionResponse(UUID id, QuestionResponseDto questionResponseDto);
    QuestionResponseDto getQuestionResponseById(UUID id);
    List<QuestionResponseDto> getQuestionResponseBySurveyResponseId(UUID surveyResponseId);
    List<QuestionResponseDto> getAllQuestionResponses();
    void deleteQuestionResponse(UUID id);
}
