package horizon.surveyservice.service;

import horizon.surveyservice.DTO.SurveyResponseDto;

import java.util.List;

public interface SurveyResponseService {
    SurveyResponseDto submitSurveyResponse(SurveyResponseDto surveyResponseDto);
    List<SurveyResponseDto> getAllSurveyResponses();
    SurveyResponseDto getSurveyResponseById(Long surveyResponseId);
    SurveyResponseDto updateSurveyResponse(Long surveyResponseId, SurveyResponseDto surveyResponseDto);
    void deleteSurveyResponse(Long surveyResponseId);
}
