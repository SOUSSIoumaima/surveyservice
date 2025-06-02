package horizon.surveyservice.mapper;

import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.entity.Option;
import horizon.surveyservice.entity.Question;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionMapper {
    public static QuestionDto toDTO(Question question) {
        if (question == null) return null;
        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionId(question.getQuestionId());
        questionDto.setQuestionText(question.getQuestionText());
        questionDto.setQuestionType(question.getQuestionType());
        questionDto.setSubject(question.getSubject());
        questionDto.setLocked(question.isLocked());
        if (question.getOptions() != null) {
            questionDto.setOptions(question.getOptions().stream().map(OptionMapper::toDto).collect(Collectors.toList()));

        }
        return questionDto;
    }
    public static Question toEntity(QuestionDto questionDto) {
        if (questionDto == null) return null;
        Question question = new Question();
        question.setQuestionId(questionDto.getQuestionId());
        question.setQuestionText(questionDto.getQuestionText());
        question.setQuestionType(questionDto.getQuestionType());
        question.setSubject(questionDto.getSubject());
        question.setLocked(questionDto.isLocked());
        if (questionDto.getOptions() != null) {
            List<Option> options = questionDto.getOptions()
                    .stream()
                    .map(optionDto -> OptionMapper.toEntity(optionDto, question))
                    .collect(Collectors.toList());
            question.setOptions(options);
        }
        return question;
    }
}
