package horizon.surveyservice.mapper;

import horizon.surveyservice.DTO.OptionResponseDto;
import horizon.surveyservice.entity.OptionResponse;
import horizon.surveyservice.entity.QuestionResponse;

public class OptionResponseMapper {
    public static OptionResponseDto toDto(OptionResponse optionResponse) {
        OptionResponseDto dto = new OptionResponseDto();
        dto.setOptionResponseId(optionResponse.getOptionResponseId());
        dto.setOptionId(optionResponse.getOptionId());
        dto.setOptionText(optionResponse.getOptionText());
        dto.setCorrect(optionResponse.isCorrect());
        dto.setSelected(optionResponse.isSelected());
        dto.setOptionScore(optionResponse.getOptionScore());
        if (optionResponse.getQuestionResponse() != null) {
            dto.setQuestionResponseId(optionResponse.getQuestionResponse().getQuestionResponseId());
        }
        return dto;
    }
    public static OptionResponse toEntity(OptionResponseDto dto, QuestionResponse questionResponse) {
        if (dto == null) {
            return null;
        }
        OptionResponse entity = new OptionResponse();
        entity.setOptionResponseId(dto.getOptionResponseId());
        entity.setOptionId(dto.getOptionId());
        entity.setOptionText(dto.getOptionText());
        entity.setCorrect(dto.isCorrect());
        entity.setSelected(dto.isSelected());
        entity.setOptionScore(dto.getOptionScore());
        entity.setQuestionResponse(questionResponse);

        return entity;
    }

}
