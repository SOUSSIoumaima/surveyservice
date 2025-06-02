package horizon.surveyservice.mapper;

import horizon.surveyservice.DTO.SurveyResponseDto;
import horizon.surveyservice.entity.SurveyResponse;

import java.util.stream.Collectors;

public class SurveyResponseMapper {
    public static SurveyResponseDto toDto(SurveyResponse entity) {
        SurveyResponseDto dto = new SurveyResponseDto();
        dto.setSurveyResponseId(entity.getSurveyResponseId());
        dto.setSurveyId(entity.getSurveyId());
        dto.setRespondentId(entity.getRespondentId());
        dto.setSubmittedAt(entity.getSubmittedAt());
        dto.setTotalScore(entity.getTotalScore());
        dto.setQuestionResponses(
                entity.getQuestionResponses() != null ?
                        entity.getQuestionResponses().stream()
                                .map(QuestionResponseMapper::toDto)
                                .collect(Collectors.toList()) : null
        );
        return dto;
    }
    public static SurveyResponse toEntity(SurveyResponseDto dto) {
        SurveyResponse entity = new SurveyResponse();
        entity.setSurveyResponseId(dto.getSurveyResponseId());
        entity.setSurveyId(dto.getSurveyId());
        entity.setRespondentId(dto.getRespondentId());
        entity.setSubmittedAt(dto.getSubmittedAt());
        entity.setTotalScore(dto.getTotalScore());

        if (dto.getQuestionResponses() != null) {
            entity.setQuestionResponses(
                    dto.getQuestionResponses().stream()
                            .map(qrDto -> QuestionResponseMapper.toEntity(qrDto, entity)) // ✅ correction ici
                            .collect(Collectors.toList())
            );
        }
        return entity;
    }
}
