package horizon.surveyservice.mapper;

import horizon.surveyservice.DTO.OptionDto;
import horizon.surveyservice.entity.Option;
import horizon.surveyservice.entity.Question;

public class OptionMapper {

    public static OptionDto toDto(Option option) {
        OptionDto dto = new OptionDto();
        dto.setOptionId(option.getOptionId());
        dto.setQuestionId(option.getQuestion() != null ? option.getQuestion().getQuestionId() : null);
        dto.setOptionText(option.getOptionText());
        dto.setCorrect(option.isCorrect());
        dto.setOptionScore(option.getOptionScore());
        dto.setLocked(option.isLocked());
        return dto;

    }
    public static Option toEntity(OptionDto optiondto, Question question) {
        Option option = new Option();
        option.setOptionId(optiondto.getOptionId());
        option.setQuestion(question);
        option.setOptionText(optiondto.getOptionText());
        option.setCorrect(optiondto.isCorrect());
        option.setOptionScore(optiondto.getOptionScore());
        option.setLocked(optiondto.isLocked());
        return option;

    }
}
