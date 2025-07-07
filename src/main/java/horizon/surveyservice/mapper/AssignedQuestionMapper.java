package horizon.surveyservice.mapper;

import horizon.surveyservice.DTO.AssignedQuestionDto;
import horizon.surveyservice.entity.AssignedQuestion;

public class AssignedQuestionMapper {
    public static AssignedQuestionDto toDto(AssignedQuestion entity) {
        if (entity == null) return null;

        AssignedQuestionDto dto = new AssignedQuestionDto();

        dto.setAssignedQuestionId(entity.getAssignedQuestionId());
        dto.setSurveyId(entity.getSurvey().getSurveyId());
        dto.setQuestionId(entity.getQuestion().getQuestionId());
        dto.setDepartmentId(entity.getDepartmentId());
        dto.setTeamId(entity.getTeamId());
        dto.setAssignedAt(entity.getAssignedAt());

        return dto;
    }

    public static AssignedQuestion toEntity(AssignedQuestionDto dto) {
        if (dto == null) return null;

        AssignedQuestion entity = new AssignedQuestion();

        entity.setAssignedQuestionId(dto.getAssignedQuestionId());
        entity.setDepartmentId(dto.getDepartmentId());
        entity.setTeamId(dto.getTeamId());
        entity.setAssignedAt(dto.getAssignedAt());

        return entity;
    }
}
