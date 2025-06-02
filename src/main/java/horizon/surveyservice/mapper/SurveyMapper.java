package horizon.surveyservice.mapper;

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
        dto.setOwnerId(survey.getOwnerId());
        dto.setType(survey.getType());
        dto.setTitle(survey.getTitle());
        dto.setDescription(survey.getDescription());
        dto.setStatus(survey.getStatus());
        dto.setCreatedAt(survey.getCreatedAt());
        dto.setUpdatedAt(survey.getUpdatedAt());
        dto.setDeadline(survey.getDeadline());
        dto.setLocked(survey.isLocked());
        if (survey.getQuestions() != null) {
            List<QuestionDto> questionsDto = survey.getQuestions()
                    .stream()
                    .map(QuestionMapper::toDTO)
                    .collect(Collectors.toList());
            dto.setQuestions(questionsDto);
        }
        return dto;
    }
    public static Survey toSurveyEntity(SurveyDto dto) {
        if (dto == null) return null;
        Survey survey = new Survey();
        survey.setSurveyId(dto.getSurveyId());
        survey.setOwnerId(dto.getOwnerId());
        survey.setType(dto.getType());
        survey.setTitle(dto.getTitle());
        survey.setDescription(dto.getDescription());
        survey.setStatus(dto.getStatus());
        survey.setCreatedAt(dto.getCreatedAt());
        survey.setUpdatedAt(dto.getUpdatedAt());
        survey.setDeadline(dto.getDeadline());
        survey.setLocked(dto.isLocked());
        if (dto.getQuestions() != null) {
            List<Question> questions = dto.getQuestions()
                    .stream()
                    .map(questionDto -> QuestionMapper.toEntity(questionDto))
                    .collect(Collectors.toList());
            survey.setQuestions(questions);
        }
        return survey;
    }

}
