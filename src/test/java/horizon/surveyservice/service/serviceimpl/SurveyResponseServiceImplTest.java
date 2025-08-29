package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.OptionResponseDto;
import horizon.surveyservice.DTO.QuestionResponseDto;
import horizon.surveyservice.DTO.SurveyResponseDto;
import horizon.surveyservice.entity.OptionResponse;
import horizon.surveyservice.entity.QuestionResponse;
import horizon.surveyservice.entity.SurveyResponse;
import horizon.surveyservice.exeptions.BadRequestException;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.repository.QuestionResponseRepository;
import horizon.surveyservice.repository.SurveyResponseRepository;
import horizon.surveyservice.util.OrganizationContextUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SurveyResponseServiceImplTest {
    private SurveyResponseRepository surveyResponseRepository;
    private QuestionResponseRepository questionResponseRepository;
    private OrganizationContextUtil organizationContextUtil;
    private SurveyResponseServiceImpl surveyResponseService;

    private UUID surveyId;
    private UUID surveyResponseId;
    private UUID userId;

    private OptionResponseDto optionResponseDto;
    private QuestionResponseDto questionResponseDto;
    private SurveyResponseDto surveyResponseDto;

    @BeforeEach
    void setUp() {
        surveyResponseRepository = mock(SurveyResponseRepository.class);
        questionResponseRepository = mock(QuestionResponseRepository.class);
        organizationContextUtil = mock(OrganizationContextUtil.class);
        surveyResponseService = new SurveyResponseServiceImpl(
                surveyResponseRepository,
                questionResponseRepository,
                organizationContextUtil
        );

        surveyId = UUID.randomUUID();
        surveyResponseId = UUID.randomUUID();
        userId = UUID.randomUUID();

        when(organizationContextUtil.getCurrentUserId()).thenReturn(userId);

        optionResponseDto = new OptionResponseDto(
                null,
                UUID.randomUUID(),
                "Option A",
                true,
                true,
                2L
        );

        questionResponseDto = new QuestionResponseDto(
                null,
                UUID.randomUUID(),
                surveyResponseId,
                "Question 1",
                null,
                LocalDateTime.now(),
                List.of(optionResponseDto)
        );

        surveyResponseDto = new SurveyResponseDto(
                null,
                surveyId,
                null,
                LocalDateTime.now(),
                null,
                List.of(questionResponseDto),
                false
        );
    }

    @Test
    void submitSurveyResponse_success() {
        when(surveyResponseRepository.save(any(SurveyResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SurveyResponseDto result = surveyResponseService.submitSurveyResponse(surveyResponseDto);

        assertNotNull(result);
        assertEquals(userId, result.getRespondentId());
        assertEquals(2L, result.getQuestionResponses().get(0).getQuestionScore());
        assertEquals(2L, result.getTotalScore());

        verify(surveyResponseRepository).save(any(SurveyResponse.class));
    }

    @Test
    void getAllSurveyResponses_success() {
        when(surveyResponseRepository.findAll()).thenReturn(List.of());

        List<SurveyResponseDto> results = surveyResponseService.getAllSurveyResponses();

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(surveyResponseRepository).findAll();
    }

    @Test
    void getSurveyResponseById_notFound() {
        UUID id = UUID.randomUUID();
        when(surveyResponseRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> surveyResponseService.getSurveyResponseById(id));
    }

    @Test
    void deleteSurveyResponse_success() {
        SurveyResponse surveyResponse = new SurveyResponse();
        surveyResponse.setSurveyResponseId(surveyResponseId);

        when(surveyResponseRepository.findById(surveyResponseId)).thenReturn(java.util.Optional.of(surveyResponse));

        surveyResponseService.deleteSurveyResponse(surveyResponseId);

        verify(surveyResponseRepository).delete(surveyResponse);
    }

    @Test
    void updateSurveyResponse_success() {
        SurveyResponse existing = new SurveyResponse();
        existing.setSurveyResponseId(surveyResponseId);
        existing.setRespondentId(userId);
        existing.setFinal(false);

        when(surveyResponseRepository.findById(surveyResponseId)).thenReturn(java.util.Optional.of(existing));
        when(surveyResponseRepository.save(any(SurveyResponse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SurveyResponseDto result = surveyResponseService.updateSurveyResponse(surveyResponseId, surveyResponseDto);

        assertNotNull(result);
        assertEquals(userId, result.getRespondentId());
        assertTrue(result.isFinal());
        assertEquals(2L, result.getTotalScore());
    }

    @Test
    void updateSurveyResponse_final_throwsException() {
        SurveyResponse existing = new SurveyResponse();
        existing.setSurveyResponseId(surveyResponseId);
        existing.setFinal(true);

        when(surveyResponseRepository.findById(surveyResponseId)).thenReturn(java.util.Optional.of(existing));

        assertThrows(BadRequestException.class,
                () -> surveyResponseService.updateSurveyResponse(surveyResponseId, surveyResponseDto));
    }
    @Test
    void submitSurveyResponse_noQuestions_totalScoreZero() {
        SurveyResponseDto emptySurvey = new SurveyResponseDto(
                null,
                surveyId,
                null,
                LocalDateTime.now(),
                null,
                List.of(),
                false
        );

        when(surveyResponseRepository.save(any(SurveyResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SurveyResponseDto result = surveyResponseService.submitSurveyResponse(emptySurvey);

        assertNotNull(result);
        assertEquals(0L, result.getTotalScore());
    }
    @Test
    void getSurveyResponseById_success() {
        SurveyResponse entity = new SurveyResponse();
        entity.setSurveyResponseId(surveyResponseId);
        when(surveyResponseRepository.findById(surveyResponseId))
                .thenReturn(Optional.of(entity));

        SurveyResponseDto result = surveyResponseService.getSurveyResponseById(surveyResponseId);

        assertNotNull(result);
        assertEquals(surveyResponseId, result.getSurveyResponseId());
    }
    @Test
    void deleteSurveyResponse_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(surveyResponseRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                surveyResponseService.deleteSurveyResponse(id));
    }
    @Test
    void updateSurveyResponse_noQuestions_totalScoreZero() {
        SurveyResponse existing = new SurveyResponse();
        existing.setSurveyResponseId(surveyResponseId);
        existing.setRespondentId(userId);
        existing.setFinal(false);

        when(surveyResponseRepository.findById(surveyResponseId)).thenReturn(Optional.of(existing));
        when(surveyResponseRepository.save(any(SurveyResponse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SurveyResponseDto emptySurvey = new SurveyResponseDto(
                surveyResponseId,
                surveyId,
                userId,
                LocalDateTime.now(),
                null,
                List.of(),
                false
        );

        SurveyResponseDto result = surveyResponseService.updateSurveyResponse(surveyResponseId, emptySurvey);

        assertNotNull(result);
        assertEquals(0L, result.getTotalScore());
        assertTrue(result.isFinal());
    }



}
