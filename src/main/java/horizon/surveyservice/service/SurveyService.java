package horizon.surveyservice.service;

import horizon.surveyservice.DTO.SurveyDto;

import java.util.List;

public interface SurveyService {
    SurveyDto createSurvey(SurveyDto surveyDto);
    List<SurveyDto> getAllSurveys();
    SurveyDto getSurveyById(Long surveyId);
    SurveyDto updateSurvey(Long surveyId, SurveyDto surveyDto);
    void deleteSurvey(Long surveyId);
    void assignQuestionToSurvey(Long surveyId, Long questionId);
    void unassignQuestionFromSurvey(Long surveyId, Long questionId);
    SurveyDto lockSurvey(Long id);
    SurveyDto unlockSurvey(Long id);
}
