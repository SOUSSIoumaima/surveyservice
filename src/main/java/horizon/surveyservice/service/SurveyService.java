package horizon.surveyservice.service;

import horizon.surveyservice.DTO.SurveyDto;

import java.util.List;
import java.util.UUID;

public interface SurveyService {
    SurveyDto createSurvey(SurveyDto surveyDto);
    List<SurveyDto> getAllSurveys();
    SurveyDto getSurveyById(UUID surveyId);
    List<SurveyDto> getSurveysByOrganization(UUID organizationId);
    SurveyDto updateSurvey(UUID surveyId, SurveyDto surveyDto);
    void deleteSurvey(UUID surveyId);
    void assignQuestionToSurvey(UUID surveyId, UUID questionId, UUID departmentId, UUID teamId);
    void unassignQuestionFromSurvey(UUID surveyId, UUID assignedQuestionId);
    SurveyDto lockSurvey(UUID id);
    SurveyDto unlockSurvey(UUID id);
    boolean exists(UUID id);
    SurveyDto publishSurvey(UUID surveyId);
    List<SurveyDto> getActiveAndClosedSurveys();

}
