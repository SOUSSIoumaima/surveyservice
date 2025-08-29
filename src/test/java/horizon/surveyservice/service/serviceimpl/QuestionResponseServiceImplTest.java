package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.QuestionResponseDto;
import horizon.surveyservice.entity.OptionResponse;
import horizon.surveyservice.entity.QuestionResponse;
import horizon.surveyservice.entity.SurveyResponse;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.mapper.OptionResponseMapper;
import horizon.surveyservice.mapper.QuestionResponseMapper;
import horizon.surveyservice.repository.QuestionResponseRepository;
import horizon.surveyservice.repository.SurveyResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QuestionResponseServiceImplTest {
    private QuestionResponseRepository questionResponseRepository;
    private SurveyResponseRepository surveyResponseRepository;
    private QuestionResponseServiceImpl questionResponseService;

    private UUID surveyResponseId;
    private UUID questionResponseId;
    private SurveyResponse surveyResponse;
    private QuestionResponse questionResponse;
    private QuestionResponseDto questionResponseDto;

    @BeforeEach
    void setUp() {
        questionResponseRepository = mock(QuestionResponseRepository.class);
        surveyResponseRepository = mock(SurveyResponseRepository.class);
        questionResponseService = new QuestionResponseServiceImpl(questionResponseRepository, surveyResponseRepository);

        surveyResponseId = UUID.randomUUID();
        questionResponseId = UUID.randomUUID();

        surveyResponse = new SurveyResponse();
        surveyResponse.setSurveyResponseId(surveyResponseId);


        OptionResponse option1 = new OptionResponse();
        option1.setSelected(true);
        option1.setOptionScore(2L);

        OptionResponse option2 = new OptionResponse();
        option2.setSelected(true);
        option2.setOptionScore(3L);


        questionResponseDto = new QuestionResponseDto();
        questionResponseDto.setSurveyResponseId(surveyResponseId);
        questionResponseDto.setOptionResponses(List.of(
                OptionResponseMapper.toDto(option1),
                OptionResponseMapper.toDto(option2)
        ));


        questionResponse = QuestionResponseMapper.toEntity(questionResponseDto, surveyResponse);
    }

    @Test
    void submitQuestionResponse_success() {
        when(surveyResponseRepository.findById(surveyResponseId)).thenReturn(Optional.of(surveyResponse));
        when(questionResponseRepository.save(any(QuestionResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        QuestionResponseDto result = questionResponseService.submitQuestionResponse(questionResponseDto);

        assertNotNull(result);
        assertEquals(5L, result.getQuestionScore()); // 2 + 3 = 5
        verify(questionResponseRepository).save(any(QuestionResponse.class));
    }

    @Test
    void submitQuestionResponse_surveyNotFound() {
        when(surveyResponseRepository.findById(surveyResponseId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> questionResponseService.submitQuestionResponse(questionResponseDto));
        assertTrue(ex.getMessage().contains("Survey Response Not Found"));
    }

    @Test
    void updateQuestionResponse_success() {
        questionResponse.setQuestionScore(5L);
        when(questionResponseRepository.findById(questionResponseId)).thenReturn(Optional.of(questionResponse));
        when(questionResponseRepository.save(any(QuestionResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        QuestionResponseDto result = questionResponseService.updateQuestionResponse(questionResponseId, questionResponseDto);

        assertNotNull(result);
        assertEquals(5L, result.getQuestionScore());
        verify(questionResponseRepository).save(questionResponse);
    }

    @Test
    void updateQuestionResponse_notFound() {
        when(questionResponseRepository.findById(questionResponseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> questionResponseService.updateQuestionResponse(questionResponseId, questionResponseDto));
    }

    @Test
    void getQuestionResponseById_success() {
        when(questionResponseRepository.findById(questionResponseId)).thenReturn(Optional.of(questionResponse));

        QuestionResponseDto result = questionResponseService.getQuestionResponseById(questionResponseId);

        assertNotNull(result);
    }

    @Test
    void getQuestionResponseById_notFound() {
        when(questionResponseRepository.findById(questionResponseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> questionResponseService.getQuestionResponseById(questionResponseId));
    }

    @Test
    void getQuestionResponseBySurveyResponseId_success() {
        when(questionResponseRepository.findBySurveyResponseSurveyResponseId(surveyResponseId))
                .thenReturn(List.of(questionResponse));

        List<QuestionResponseDto> results = questionResponseService.getQuestionResponseBySurveyResponseId(surveyResponseId);

        assertEquals(1, results.size());
    }

    @Test
    void getQuestionResponseBySurveyResponseId_notFound() {
        when(questionResponseRepository.findBySurveyResponseSurveyResponseId(surveyResponseId))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> questionResponseService.getQuestionResponseBySurveyResponseId(surveyResponseId));
    }

    @Test
    void getAllQuestionResponses_success() {
        when(questionResponseRepository.findAll()).thenReturn(List.of(questionResponse));

        List<QuestionResponseDto> results = questionResponseService.getAllQuestionResponses();

        assertEquals(1, results.size());
    }

    @Test
    void deleteQuestionResponse_success() {
        when(questionResponseRepository.findById(questionResponseId)).thenReturn(Optional.of(questionResponse));

        questionResponseService.deleteQuestionResponse(questionResponseId);

        verify(questionResponseRepository).delete(questionResponse);
    }

    @Test
    void deleteQuestionResponse_notFound() {
        when(questionResponseRepository.findById(questionResponseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> questionResponseService.deleteQuestionResponse(questionResponseId));
    }
}
