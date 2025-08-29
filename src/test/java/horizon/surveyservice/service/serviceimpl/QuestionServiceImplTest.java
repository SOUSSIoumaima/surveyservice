package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.OptionDto;
import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.entity.Option;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Test
    void getAllQuestions_withInvalidAccess_shouldFilterOut() {
        Question q1 = new Question();
        q1.setQuestionId(UUID.randomUUID());
        q1.setOrganizationId(orgId);

        when(questionRepository.findAll()).thenReturn(List.of(q1));
        // simulate accès refusé
        doThrow(new RuntimeException("Access denied"))
                .when(organizationContextUtil).validateOrganizationAccess(any());

        var result = questionService.getAllQuestions();

        assertTrue(result.isEmpty()); // maintenant la question est filtrée
    }

    @Test
    void updateQuestion_shouldUpdateFieldsAndOptions() {
        // Arrange
        UUID questionId = UUID.randomUUID();
        Question existingQuestion = new Question();
        existingQuestion.setQuestionId(questionId);
        existingQuestion.setSubject("Old Subject");
        existingQuestion.setQuestionText("Old Text");
        existingQuestion.setOptions(new ArrayList<>()); // ✅ éviter le NullPointerException

        QuestionDto updatedDto = new QuestionDto();
        updatedDto.setQuestionId(questionId);
        updatedDto.setSubject("New Subject");
        updatedDto.setQuestionText("New Text");

        // Créer les OptionDto correctement
        OptionDto o1 = new OptionDto();
        o1.setOptionText("Option 1");
        OptionDto o2 = new OptionDto();
        o2.setOptionText("Option 2");

        updatedDto.setOptions(List.of(o1, o2));

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(existingQuestion));
        when(questionRepository.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        QuestionDto result = questionService.updateQuestion(questionId, updatedDto);

        // Assert
        assertEquals("New Subject", result.getSubject());
        assertEquals("New Text", result.getQuestionText());
        assertEquals(2, result.getOptions().size());
        assertEquals("Option 1", result.getOptions().get(0).getOptionText());
        assertEquals("Option 2", result.getOptions().get(1).getOptionText());
        verify(questionRepository).save(existingQuestion);
    }


    @Test
    void updateQuestion_locked_shouldThrowException() {
        question.setLocked(true);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        doNothing().when(organizationContextUtil).validateOrganizationAccess(orgId);

        QuestionDto dto = new QuestionDto();
        assertThrows(LockedException.class, () -> questionService.updateQuestion(questionId, dto));
    }
    @Test
    void getBySubject_shouldReturnOnlyAccessibleQuestions() {
        Question q = new Question();
        q.setQuestionId(questionId);
        q.setOrganizationId(orgId);

        when(questionRepository.findBySubjectContainingIgnoreCase("Math")).thenReturn(List.of(q));
        doNothing().when(organizationContextUtil).validateOrganizationAccess(orgId);

        var result = questionService.getBySubject("Math");

        assertEquals(1, result.size());
    }
    @Test
    void getQuestionByOrganization_shouldReturnDtos() {
        when(questionRepository.findByOrganizationId(orgId)).thenReturn(List.of(question));

        var result = questionService.getQuestionByOrganization(orgId);

        assertEquals(1, result.size());
        assertEquals(questionId, result.get(0).getQuestionId());
    }
    @Test
    void updateQuestion_optionLocked_shouldThrowException() {
        // Option existante verrouillée
        Option lockedOption = new Option();
        lockedOption.setOptionId(UUID.randomUUID());
        lockedOption.setLocked(true);
        question.setOptions(List.of(lockedOption));

        // Créer l'OptionDto via setters
        OptionDto dtoOption = new OptionDto();
        dtoOption.setOptionId(lockedOption.getOptionId());
        dtoOption.setOptionText("Updated");
        dtoOption.setCorrect(true);
        dtoOption.setOptionScore(5L);
        dtoOption.setLocked(true);

        QuestionDto dto = new QuestionDto();
        dto.setOptions(List.of(dtoOption));

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        doNothing().when(organizationContextUtil).validateOrganizationAccess(orgId);
        when(optionRepository.findById(lockedOption.getOptionId())).thenReturn(Optional.of(lockedOption));

        assertThrows(LockedException.class, () -> questionService.updateQuestion(questionId, dto));
    }
    @Test
    void getBySubjectAndOrganization_shouldReturnDtos() {
        Question q = new Question();
        q.setQuestionId(questionId);
        q.setOrganizationId(orgId);

        when(questionRepository.findBySubjectContainingIgnoreCaseAndOrganizationId("Math", orgId))
                .thenReturn(List.of(q));

        var result = questionService.getBySubjectAndOrganization("Math", orgId);

        assertEquals(1, result.size());
        assertEquals(questionId, result.get(0).getQuestionId());
    }

    @Test
    void getBySubjectAndOrganization_emptyList_shouldReturnEmpty() {
        when(questionRepository.findBySubjectContainingIgnoreCaseAndOrganizationId("Math", orgId))
                .thenReturn(List.of());

        var result = questionService.getBySubjectAndOrganization("Math", orgId);

        assertTrue(result.isEmpty());
    }

    @Test
    void lockQuestion_notFound_shouldThrowException() {
        UUID randomId = UUID.randomUUID();
        when(questionRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> questionService.lockQuestion(randomId));
    }

    @Test
    void unlockQuestion_notFound_shouldThrowException() {
        UUID randomId = UUID.randomUUID();
        when(questionRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> questionService.unlockQuestion(randomId));
    }

    @Test
    void updateQuestion_notFound_shouldThrowException() {
        UUID randomId = UUID.randomUUID();
        when(questionRepository.findById(randomId)).thenReturn(Optional.empty());

        QuestionDto dto = new QuestionDto();
        assertThrows(ResourceNotFoundException.class, () -> questionService.updateQuestion(randomId, dto));
    }

    @Test
    void updateQuestion_createNewOption_shouldAddOption() {
        // Existing question with empty options
        Question existingQuestion = new Question();
        existingQuestion.setQuestionId(questionId);
        existingQuestion.setOptions(new ArrayList<>());
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(existingQuestion));
        doNothing().when(organizationContextUtil).validateOrganizationAccess(orgId);
        when(questionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // DTO with a new option (no optionId)
        OptionDto newOption = new OptionDto();
        newOption.setOptionText("New Option");
        newOption.setCorrect(true);
        newOption.setOptionScore(5L);
        newOption.setLocked(false);

        QuestionDto dto = new QuestionDto();
        dto.setOptions(List.of(newOption));

        QuestionDto result = questionService.updateQuestion(questionId, dto);

        assertEquals(1, result.getOptions().size());
        assertEquals("New Option", result.getOptions().get(0).getOptionText());
    }

    @Test
    void getAllQuestions_multipleQuestions_someAccessible_someNot() {
        Question q1 = new Question();
        q1.setQuestionId(UUID.randomUUID());
        q1.setOrganizationId(orgId);

        Question q2 = new Question();
        q2.setQuestionId(UUID.randomUUID());
        q2.setOrganizationId(UUID.randomUUID()); // inaccessible

        when(questionRepository.findAll()).thenReturn(List.of(q1, q2));
        doNothing().when(organizationContextUtil).validateOrganizationAccess(orgId);
        doThrow(new RuntimeException("Access denied")).when(organizationContextUtil)
                .validateOrganizationAccess(q2.getOrganizationId());

        var result = questionService.getAllQuestions();

        assertEquals(1, result.size());
        assertEquals(q1.getQuestionId(), result.get(0).getQuestionId());
    }




}
