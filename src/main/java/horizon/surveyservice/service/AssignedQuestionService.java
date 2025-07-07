package horizon.surveyservice.service;

import horizon.surveyservice.DTO.AssignedQuestionDto;

import java.util.List;
import java.util.UUID;

public interface AssignedQuestionService {
    AssignedQuestionDto assignQuestionToSurvey(UUID surveyId, UUID questionId,UUID departmentId, UUID teamId);
    List<AssignedQuestionDto> getAssignedQuestionsBySurvey(UUID surveyId);
    void unassignQuestionFromSurvey(UUID surveyId, UUID questionId);
}
