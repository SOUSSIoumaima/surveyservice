package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.AssignedQuestionDto;
import horizon.surveyservice.entity.AssignedQuestion;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.entity.Survey;
import horizon.surveyservice.mapper.AssignedQuestionMapper;
import horizon.surveyservice.repository.AssignedQuestionRepository;
import horizon.surveyservice.repository.QuestionRepository;
import horizon.surveyservice.repository.SurveyRepository;
import horizon.surveyservice.service.AssignedQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignedQuestionServiceImpl implements AssignedQuestionService {
    private final AssignedQuestionRepository assignedQuestionRepository;
    private final QuestionRepository questionRepository;
    private final SurveyRepository surveyRepository;
    public AssignedQuestionServiceImpl(AssignedQuestionRepository assignedQuestionRepository,
                                       SurveyRepository surveyRepository,
                                       QuestionRepository questionRepository) {
        this.assignedQuestionRepository = assignedQuestionRepository;
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
    }
    public AssignedQuestionDto assignQuestionToSurvey(UUID surveyId, UUID questionId, UUID departmentId, UUID teamId) {
        if (isQuestionAssignedToSurvey(surveyId, questionId)) {
            throw new IllegalStateException("This question is already assigned to the survey.");
        }
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        AssignedQuestion assignedQuestion = new AssignedQuestion();
        assignedQuestion.setSurvey(survey);
        assignedQuestion.setQuestion(question);
        assignedQuestion.setDepartmentId(departmentId);
        assignedQuestion.setTeamId(teamId);
        assignedQuestion.setAssignedAt(LocalDateTime.now());

        AssignedQuestion saved = assignedQuestionRepository.save(assignedQuestion);

        return AssignedQuestionMapper.toDto(saved);
    }

    @Override
    public List<AssignedQuestionDto> getAssignedQuestionsBySurvey(UUID surveyId) {
        List<AssignedQuestion> assignedQuestions = assignedQuestionRepository.findBySurvey_SurveyId(surveyId);
        return assignedQuestions.stream()
                .map(AssignedQuestionMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public void unassignQuestionFromSurvey(UUID surveyId, UUID questionId) {
        assignedQuestionRepository.deleteBySurvey_SurveyIdAndQuestion_QuestionId(surveyId, questionId);
    }
    @Override
    public boolean isQuestionAssignedToSurvey(UUID surveyId, UUID questionId) {
        return assignedQuestionRepository.existsBySurvey_SurveyIdAndQuestion_QuestionId(surveyId, questionId);
    }
}

