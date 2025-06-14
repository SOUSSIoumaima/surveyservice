package horizon.surveyservice.service;

import horizon.surveyservice.DTO.SurveyResponseDto;

import java.util.List;
import java.util.UUID;

public interface SurveyResponseService {
    SurveyResponseDto submitSurveyResponse(SurveyResponseDto surveyResponseDto);
    List<SurveyResponseDto> getAllSurveyResponses();
    SurveyResponseDto getSurveyResponseById(UUID surveyResponseId);
    SurveyResponseDto updateSurveyResponse(UUID surveyResponseId, SurveyResponseDto surveyResponseDto);
    void deleteSurveyResponse(UUID surveyResponseId);
}
