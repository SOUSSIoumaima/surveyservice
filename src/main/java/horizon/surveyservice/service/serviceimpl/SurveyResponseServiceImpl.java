package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.SurveyResponseDto;
import horizon.surveyservice.entity.SurveyResponse;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.mapper.SurveyResponseMapper;
import horizon.surveyservice.repository.QuestionResponseRepository;
import horizon.surveyservice.repository.SurveyResponseRepository;
import horizon.surveyservice.service.SurveyResponseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class SurveyResponseServiceImpl implements SurveyResponseService {

    private final SurveyResponseRepository surveyResponseRepository;
    private final QuestionResponseRepository questionResponseRepository;
    public SurveyResponseServiceImpl(SurveyResponseRepository surveyResponseRepository, QuestionResponseRepository questionResponseRepository) {
        this.surveyResponseRepository = surveyResponseRepository;
        this.questionResponseRepository = questionResponseRepository;
    }
    @Override
    public SurveyResponseDto submitSurveyResponse(SurveyResponseDto surveyResponseDto) {
        SurveyResponse surveyResponse = SurveyResponseMapper.toEntity(surveyResponseDto);
        if (surveyResponse.getQuestionResponses() != null) {
            surveyResponse.getQuestionResponses().forEach(questionResponse -> {
                questionResponse.setSurveyResponse(surveyResponse);

                if (questionResponse.getOptionResponses() != null) {
                    questionResponse.getOptionResponses().forEach(optionResponse -> {
                        optionResponse.setQuestionResponse(questionResponse);
                    });
                }
            });
        }
        SurveyResponse saved = surveyResponseRepository.save(surveyResponse);
        return SurveyResponseMapper.toDto(saved);
    }

    @Override
    public List<SurveyResponseDto> getAllSurveyResponses() {
        List<SurveyResponse> surveyResponses = surveyResponseRepository.findAll();
        return surveyResponses.stream()
                .map(SurveyResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SurveyResponseDto getSurveyResponseById(UUID surveyResponseId) {
        SurveyResponse surveyResponse = surveyResponseRepository.findById(surveyResponseId)
                .orElseThrow(()-> new ResourceNotFoundException("Survey Response Not Found with id: " + surveyResponseId));
        return SurveyResponseMapper.toDto(surveyResponse);
    }

    @Override
    public SurveyResponseDto updateSurveyResponse(UUID surveyResponseId, SurveyResponseDto surveyResponseDto) {
        SurveyResponse existing= surveyResponseRepository.findById(surveyResponseId)
                .orElseThrow(()-> new ResourceNotFoundException("Survey Response Not Found with id: " + surveyResponseId));
        existing.setSubmittedAt(surveyResponseDto.getSubmittedAt());
        existing.setTotalScore(surveyResponseDto.getTotalScore());
        SurveyResponse updated = surveyResponseRepository.save(existing);
        return SurveyResponseMapper.toDto(updated);
    }

    @Override
    public void deleteSurveyResponse(UUID surveyResponseId) {
        SurveyResponse existing = surveyResponseRepository.findById(surveyResponseId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey Response Not Found with id: " + surveyResponseId));
        surveyResponseRepository.delete(existing);
    }
}
