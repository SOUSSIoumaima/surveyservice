package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.AssignedQuestionDto;
import horizon.surveyservice.entity.AssignedQuestion;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.entity.Survey;
import horizon.surveyservice.repository.AssignedQuestionRepository;
import horizon.surveyservice.repository.QuestionRepository;
import horizon.surveyservice.repository.SurveyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AssignedQuestionServiceImplTest {
    @Mock
    private AssignedQuestionRepository assignedQuestionRepository;

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private AssignedQuestionServiceImpl assignedQuestionService;

    private UUID surveyId;
    private UUID questionId;
    private Survey survey;
    private Question question;
    private AssignedQuestion assignedQuestion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        surveyId = UUID.randomUUID();
        questionId = UUID.randomUUID();

        survey = new Survey();
        survey.setSurveyId(surveyId);

        question = new Question();
        question.setQuestionId(questionId);

        assignedQuestion = new AssignedQuestion();
        assignedQuestion.setAssignedQuestionId(UUID.randomUUID());
        assignedQuestion.setSurvey(survey);
        assignedQuestion.setQuestion(question);
        assignedQuestion.setAssignedAt(LocalDateTime.now());
    }

    @Test
    void testAssignQuestionToSurvey_success() {
        when(assignedQuestionRepository.existsBySurvey_SurveyIdAndQuestion_QuestionId(surveyId, questionId))
                .thenReturn(false);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(assignedQuestionRepository.save(any())).thenReturn(assignedQuestion);

        AssignedQuestionDto dto = assignedQuestionService.assignQuestionToSurvey(surveyId, questionId, null, null);

        assertNotNull(dto);
        assertEquals(surveyId, dto.getSurveyId());
        verify(assignedQuestionRepository, times(1)).save(any());
    }

    @Test
    void testAssignQuestionToSurvey_alreadyAssigned() {
        when(assignedQuestionRepository.existsBySurvey_SurveyIdAndQuestion_QuestionId(surveyId, questionId))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                assignedQuestionService.assignQuestionToSurvey(surveyId, questionId, null, null));
    }

    @Test
    void testGetAssignedQuestionsBySurvey() {
        when(assignedQuestionRepository.findBySurvey_SurveyId(surveyId))
                .thenReturn(Collections.singletonList(assignedQuestion));

        List<AssignedQuestionDto> list = assignedQuestionService.getAssignedQuestionsBySurvey(surveyId);

        assertEquals(1, list.size());
        assertEquals(surveyId, list.get(0).getSurveyId());
    }

    @Test
    void testUnassignQuestionFromSurvey() {
        doNothing().when(assignedQuestionRepository)
                .deleteBySurvey_SurveyIdAndQuestion_QuestionId(surveyId, questionId);

        assignedQuestionService.unassignQuestionFromSurvey(surveyId, questionId);

        verify(assignedQuestionRepository, times(1))
                .deleteBySurvey_SurveyIdAndQuestion_QuestionId(surveyId, questionId);
    }

    @Test
    void testIsQuestionAssignedToSurvey() {
        when(assignedQuestionRepository.existsBySurvey_SurveyIdAndQuestion_QuestionId(surveyId, questionId))
                .thenReturn(true);

        assertTrue(assignedQuestionService.isQuestionAssignedToSurvey(surveyId, questionId));
    }
}
