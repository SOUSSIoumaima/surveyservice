package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.SurveyResponseDto;
import horizon.surveyservice.entity.OptionResponse;
import horizon.surveyservice.entity.QuestionResponse;
import horizon.surveyservice.entity.SurveyResponse;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.mapper.SurveyResponseMapper;
import horizon.surveyservice.repository.QuestionResponseRepository;
import horizon.surveyservice.repository.SurveyResponseRepository;
import horizon.surveyservice.service.SurveyResponseService;
import horizon.surveyservice.util.OrganizationContextUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class SurveyResponseServiceImpl implements SurveyResponseService {

    private final SurveyResponseRepository surveyResponseRepository;
    private final QuestionResponseRepository questionResponseRepository;
    private final OrganizationContextUtil organizationContextUtil;
    public SurveyResponseServiceImpl(SurveyResponseRepository surveyResponseRepository, QuestionResponseRepository questionResponseRepository, OrganizationContextUtil organizationContextUtil) {
        this.surveyResponseRepository = surveyResponseRepository;
        this.questionResponseRepository = questionResponseRepository;
        this.organizationContextUtil = organizationContextUtil;
    }
    @Override
    public SurveyResponseDto submitSurveyResponse(SurveyResponseDto surveyResponseDto) {
        SurveyResponse surveyResponse = SurveyResponseMapper.toEntity(surveyResponseDto);


        UUID currentUserId = organizationContextUtil.getCurrentUserId();
        surveyResponse.setRespondentId(currentUserId);

        if (surveyResponse.getQuestionResponses() != null && !surveyResponse.getQuestionResponses().isEmpty()) {
            surveyResponse.getQuestionResponses().stream()
                    .filter(q -> q != null)
                    .forEach(questionResponse -> {
                        questionResponse.setSurveyResponse(surveyResponse);

                        if (questionResponse.getOptionResponses() != null && !questionResponse.getOptionResponses().isEmpty()) {
                            long questionScore = questionResponse.getOptionResponses().stream()
                                    .filter(o -> o != null && o.isSelected())
                                    .mapToLong(o -> o.getOptionScore() != null ? o.getOptionScore() : 0L)
                                    .sum();
                            questionResponse.setQuestionScore(questionScore);

                            questionResponse.getOptionResponses().forEach(optionResponse -> {
                                if (optionResponse != null) {
                                    optionResponse.setQuestionResponse(questionResponse);
                                }
                            });
                        } else {
                            questionResponse.setQuestionScore(0L);
                        }
                    });

            // Calcul du score total du survey
            long totalScore = surveyResponse.getQuestionResponses().stream()
                    .filter(q -> q != null)
                    .mapToLong(q -> q.getQuestionScore() != null ? q.getQuestionScore() : 0L)
                    .sum();
            surveyResponse.setTotalScore(totalScore);
        } else {
            surveyResponse.setTotalScore(0L); // si pas de questions
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
        existing.setFinal(surveyResponseDto.isFinal());
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
