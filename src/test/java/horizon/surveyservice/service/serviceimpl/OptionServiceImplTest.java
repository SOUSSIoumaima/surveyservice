package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.OptionDto;
import horizon.surveyservice.entity.Option;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.exeptions.LockedException;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.mapper.OptionMapper;
import horizon.surveyservice.repository.OptionRepository;
import horizon.surveyservice.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OptionServiceImplTest {
    private OptionRepository optionRepository;
    private QuestionRepository questionRepository;
    private OptionServiceImpl optionService;

    private UUID optionId;
    private UUID questionId;
    private Question question;
    private Option option;
    private OptionDto optionDto;

    @BeforeEach
    void setUp() {
        optionRepository = mock(OptionRepository.class);
        questionRepository = mock(QuestionRepository.class);
        optionService = new OptionServiceImpl(optionRepository, questionRepository);

        optionId = UUID.randomUUID();
        questionId = UUID.randomUUID();

        question = new Question();
        question.setQuestionId(questionId);

        option = new Option();
        option.setOptionId(optionId);
        option.setOptionText("Option A");
        option.setCorrect(true);
        option.setOptionScore(2L);
        option.setLocked(false);
        option.setQuestion(question);

        // Utilise le mapper pour obtenir un OptionDto (si ton mapper est "pure")
        optionDto = OptionMapper.toDto(option);
    }

    @Test
    void createOption_success() {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(optionRepository.save(any(Option.class))).thenReturn(option);

        OptionDto result = optionService.createOption(optionDto);

        assertNotNull(result);
        assertEquals("Option A", result.getOptionText());
        verify(optionRepository).save(any(Option.class));
    }

    @Test
    void createOption_questionNotFound() {
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> optionService.createOption(optionDto));

        assertTrue(ex.getMessage().contains("Question not found"));
    }

    @Test
    void updateOption_success() {
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(optionRepository.save(any(Option.class))).thenReturn(option);

        optionDto.setOptionText("Updated text");
        OptionDto result = optionService.updateOption(optionId, optionDto);

        assertEquals("Updated text", result.getOptionText());
        verify(optionRepository).save(option);
    }

    @Test
    void updateOption_notFound() {
        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> optionService.updateOption(optionId, optionDto));
    }

    @Test
    void updateOption_locked() {
        option.setLocked(true);
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));

        assertThrows(LockedException.class,
                () -> optionService.updateOption(optionId, optionDto));
    }

    @Test
    void getOptionById_success() {
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));

        OptionDto result = optionService.getOptionById(optionId);

        assertEquals("Option A", result.getOptionText());
    }

    @Test
    void getOptionById_notFound() {
        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> optionService.getOptionById(optionId));
    }

    @Test
    void getOptionByQuestionId_success() {
        when(optionRepository.findByQuestionQuestionId(questionId))
                .thenReturn(List.of(option));

        List<OptionDto> results = optionService.getOptionByQuestionId(questionId);

        assertEquals(1, results.size());
        assertEquals("Option A", results.get(0).getOptionText());
    }

    @Test
    void getOptionByQuestionId_notFound() {
        when(optionRepository.findByQuestionQuestionId(questionId))
                .thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class,
                () -> optionService.getOptionByQuestionId(questionId));
    }

    @Test
    void getAllOptions_success() {
        when(optionRepository.findAll()).thenReturn(List.of(option));

        List<OptionDto> results = optionService.getAllOptions();

        assertEquals(1, results.size());
    }

    @Test
    void deleteOption_success() {
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));

        optionService.deleteOption(optionId);

        verify(optionRepository).delete(option);
    }

    @Test
    void deleteOption_notFound() {
        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> optionService.deleteOption(optionId));
    }

    @Test
    void deleteOption_locked() {
        option.setLocked(true);
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));

        assertThrows(LockedException.class,
                () -> optionService.deleteOption(optionId));
    }

    @Test
    void lockOption_success() {
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(optionRepository.save(any(Option.class))).thenReturn(option);

        OptionDto result = optionService.lockOption(optionId);

        assertTrue(result.isLocked());
    }

    @Test
    void lockOption_notFound() {
        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> optionService.lockOption(optionId));
    }

    @Test
    void unlockOption_success() {
        option.setLocked(true);
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(optionRepository.save(any(Option.class))).thenReturn(option);

        OptionDto result = optionService.unlockOption(optionId);

        assertFalse(result.isLocked());
    }

    @Test
    void unlockOption_notFound() {
        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> optionService.unlockOption(optionId));
    }
}
