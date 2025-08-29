package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.OptionResponseDto;
import horizon.surveyservice.entity.OptionResponse;
import horizon.surveyservice.entity.QuestionResponse;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.repository.OptionResponseRepository;
import horizon.surveyservice.repository.QuestionResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OptionResponseServiceImplTest {
    private OptionResponseRepository optionResponseRepository;
    private QuestionResponseRepository questionResponseRepository;
    private OptionResponseServiceImpl optionResponseService;

    private UUID optionResponseId;
    private UUID questionResponseId;
    private QuestionResponse questionResponse;
    private OptionResponse optionResponse;
    private OptionResponseDto optionResponseDto;

    @BeforeEach
    void setUp() {
        optionResponseRepository = mock(OptionResponseRepository.class);
        questionResponseRepository = mock(QuestionResponseRepository.class);
        optionResponseService = new OptionResponseServiceImpl(optionResponseRepository, questionResponseRepository);

        optionResponseId = UUID.randomUUID();
        questionResponseId = UUID.randomUUID();

        questionResponse = new QuestionResponse();
        questionResponse.setQuestionResponseId(questionResponseId);

        optionResponse = new OptionResponse();
        optionResponse.setOptionResponseId(optionResponseId);
        optionResponse.setSelected(true);
        optionResponse.setQuestionResponse(questionResponse);

        optionResponseDto = new OptionResponseDto();
        optionResponseDto.setOptionResponseId(optionResponseId);
        optionResponseDto.setQuestionResponseId(questionResponseId);
        optionResponseDto.setSelected(true);
    }

    @Test
    void submitOptionResponse_success() {
        when(questionResponseRepository.findById(questionResponseId)).thenReturn(Optional.of(questionResponse));
        when(optionResponseRepository.save(any(OptionResponse.class))).thenReturn(optionResponse);

        OptionResponseDto result = optionResponseService.submitOptionResponse(optionResponseDto);

        assertNotNull(result);
        assertTrue(result.isSelected());
        verify(optionResponseRepository).save(any(OptionResponse.class));
    }

    @Test
    void submitOptionResponse_questionResponseNotFound() {
        when(questionResponseRepository.findById(questionResponseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> optionResponseService.submitOptionResponse(optionResponseDto));
    }

    @Test
    void updateOptionResponse_success() {
        when(optionResponseRepository.findById(optionResponseId)).thenReturn(Optional.of(optionResponse));
        when(optionResponseRepository.save(any(OptionResponse.class))).thenReturn(optionResponse);

        optionResponseDto.setSelected(false);
        OptionResponseDto result = optionResponseService.updateOptionResponse(optionResponseId, optionResponseDto);

        assertFalse(result.isSelected());
        verify(optionResponseRepository).save(optionResponse);
    }

    @Test
    void updateOptionResponse_notFound() {
        when(optionResponseRepository.findById(optionResponseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> optionResponseService.updateOptionResponse(optionResponseId, optionResponseDto));
    }

    @Test
    void getOptionResponseById_success() {
        when(optionResponseRepository.findById(optionResponseId)).thenReturn(Optional.of(optionResponse));

        OptionResponseDto result = optionResponseService.getOptionResponseById(optionResponseId);

        assertNotNull(result);
        assertTrue(result.isSelected());
    }

    @Test
    void getOptionResponseById_notFound() {
        when(optionResponseRepository.findById(optionResponseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> optionResponseService.getOptionResponseById(optionResponseId));
    }

    @Test
    void getOptionResponseByQuestionResponseId_success() {
        when(optionResponseRepository.findByQuestionResponseQuestionResponseId(questionResponseId))
                .thenReturn(List.of(optionResponse));

        List<OptionResponseDto> results = optionResponseService.getOptionResponseByQuestionResponseId(questionResponseId);

        assertEquals(1, results.size());
    }

    @Test
    void getOptionResponseByQuestionResponseId_notFound() {
        when(optionResponseRepository.findByQuestionResponseQuestionResponseId(questionResponseId))
                .thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> optionResponseService.getOptionResponseByQuestionResponseId(questionResponseId));
    }

    @Test
    void getAllOptionResponses_success() {
        when(optionResponseRepository.findAll()).thenReturn(List.of(optionResponse));

        List<OptionResponseDto> results = optionResponseService.getAllOptionResponses();

        assertEquals(1, results.size());
    }

    @Test
    void deleteOptionResponse_success() {
        when(optionResponseRepository.findById(optionResponseId)).thenReturn(Optional.of(optionResponse));

        optionResponseService.deleteOptionResponse(optionResponseId);

        verify(optionResponseRepository).delete(optionResponse);
    }

    @Test
    void deleteOptionResponse_notFound() {
        when(optionResponseRepository.findById(optionResponseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> optionResponseService.deleteOptionResponse(optionResponseId));
    }
}
