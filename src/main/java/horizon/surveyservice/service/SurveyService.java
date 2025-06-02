package horizon.surveyservice.service;

import horizon.surveyservice.DTO.SurveyDto;

import java.util.List;
import java.util.UUID;

public interface SurveyService {
    SurveyDto createSurvey(SurveyDto surveyDto);
    List<SurveyDto> getAllSurveys();
    SurveyDto getSurveyById(Long surveyId);
    SurveyDto updateSurvey(Long surveyId, SurveyDto surveyDto);
    void deleteSurvey(Long surveyId);
    void assignQuestionToSurvey(Long surveyId, UUID questionId);
    void unassignQuestionFromSurvey(Long surveyId, UUID questionId);
    SurveyDto lockSurvey(Long id);
    SurveyDto unlockSurvey(Long id);
}
