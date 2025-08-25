package horizon.surveyservice.mapper;

import horizon.surveyservice.DTO.AssignedQuestionDto;
import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.DTO.SurveyDto;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.entity.Survey;

import java.util.List;
import java.util.stream.Collectors;

public class SurveyMapper {

    public static SurveyDto toSurveyDto(Survey survey) {
        if (survey == null) return null;

        SurveyDto dto = new SurveyDto();
        dto.setSurveyId(survey.getSurveyId());
        dto.setOrganizationId(survey.getOrganizationId());
        dto.setType(survey.getType());
        dto.setTitle(survey.getTitle());
        dto.setOwnerId(survey.getOwnerId());
        dto.setDescription(survey.getDescription());
        dto.setStatus(survey.getStatus());
        dto.setCreatedAt(survey.getCreatedAt());
        dto.setUpdatedAt(survey.getUpdatedAt());
        dto.setDeadline(survey.getDeadline());
        dto.setLocked(survey.isLocked());
        dto.setResponseType(survey.getResponseType());

        if (survey.getAssignedQuestions() != null && !survey.getAssignedQuestions().isEmpty()) {
            List<AssignedQuestionDto> assignedQuestionDtos = survey.getAssignedQuestions().stream()
                    .map(AssignedQuestionMapper::toDto)
                    .collect(Collectors.toList());
            dto.setAssignedQuestions(assignedQuestionDtos);
        }

        return dto;
    }

    public static Survey toSurveyEntity(SurveyDto dto) {
        if (dto == null) return null;

        Survey survey = new Survey();
        survey.setSurveyId(dto.getSurveyId());
        survey.setOrganizationId(dto.getOrganizationId());
        survey.setType(dto.getType());
        survey.setTitle(dto.getTitle());
        survey.setOwnerId(dto.getOwnerId());
        survey.setDescription(dto.getDescription());
        survey.setStatus(dto.getStatus());
        survey.setCreatedAt(dto.getCreatedAt());
        survey.setUpdatedAt(dto.getUpdatedAt());
        survey.setDeadline(dto.getDeadline());
        survey.setLocked(dto.isLocked());
        survey.setResponseType(dto.getResponseType());
        return survey;
    }
}

