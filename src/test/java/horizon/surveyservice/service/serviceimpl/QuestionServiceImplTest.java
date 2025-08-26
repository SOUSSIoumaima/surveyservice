package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.exeptions.LockedException;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.mapper.QuestionMapper;
import horizon.surveyservice.repository.OptionRepository;
import horizon.surveyservice.repository.QuestionRepository;
import horizon.surveyservice.util.OrganizationContextUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class QuestionServiceImplTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private OrganizationContextUtil organizationContextUtil;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private UUID orgId;
    private UUID questionId;
    private Question question;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orgId = UUID.randomUUID();
        questionId = UUID.randomUUID();
        question = new Question();
        question.setQuestionId(questionId);
        question.setOrganizationId(orgId);
        question.setLocked(false);
        question.setSubject("Math");
        question.setQuestionText("What is 2+2?");
    }

    @Test
    void createQuestion_shouldReturnSavedQuestion() {
        QuestionDto dto = QuestionMapper.toDTO(question);
        when(organizationContextUtil.getCurrentOrganizationId()).thenReturn(orgId);
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        QuestionDto result = questionService.createQuestion(dto);

        assertNotNull(result);
        assertEquals(question.getQuestionId(), result.getQuestionId());
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void getQuestionById_existingQuestion_shouldReturnDto() {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        doNothing().when(organizationContextUtil).validateOrganizationAccess(orgId);

        QuestionDto result = questionService.getQuestionById(questionId);

        assertNotNull(result);
        assertEquals(questionId, result.getQuestionId());
    }

    @Test
    void getQuestionById_notFound_shouldThrowException() {
        when(questionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> questionService.getQuestionById(UUID.randomUUID()));
    }

    @Test
    void lockQuestion_shouldSetLockedTrue() {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        doNothing().when(organizationContextUtil).validateOrganizationAccess(orgId);

        when(questionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        QuestionDto result = questionService.lockQuestion(questionId);

        assertTrue(result.isLocked());
        verify(questionRepository, times(1)).save(any());
    }


    @Test
    void unlockQuestion_shouldSetLockedFalse() {
        question.setLocked(true);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        doNothing().when(organizationContextUtil).validateOrganizationAccess(orgId);
        when(questionRepository.save(any())).thenReturn(question);

        QuestionDto result = questionService.unlockQuestion(questionId);

        assertFalse(result.isLocked());
        verify(questionRepository, times(1)).save(any());
    }

    @Test
    void deleteQuestion_locked_shouldThrowException() {
        question.setLocked(true);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        doNothing().when(organizationContextUtil).validateOrganizationAccess(orgId);

        assertThrows(LockedException.class, () -> questionService.deleteQuestion(questionId));
    }

    @Test
    void deleteQuestion_unlocked_shouldCallDelete() {
        question.setLocked(false);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        doNothing().when(organizationContextUtil).validateOrganizationAccess(orgId);

        questionService.deleteQuestion(questionId);

        verify(questionRepository, times(1)).deleteById(questionId);
    }
}
