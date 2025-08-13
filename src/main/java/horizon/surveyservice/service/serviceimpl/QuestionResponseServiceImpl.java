package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.QuestionResponseDto;
import horizon.surveyservice.entity.OptionResponse;
import horizon.surveyservice.entity.QuestionResponse;
import horizon.surveyservice.entity.SurveyResponse;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.mapper.QuestionResponseMapper;
import horizon.surveyservice.repository.QuestionResponseRepository;
import horizon.surveyservice.repository.SurveyResponseRepository;
import horizon.surveyservice.service.QuestionResponseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class QuestionResponseServiceImpl implements QuestionResponseService {
    private final QuestionResponseRepository questionResponseRepository;
    private final SurveyResponseRepository surveyResponseRepository;

    public QuestionResponseServiceImpl(QuestionResponseRepository questionResponseRepository,SurveyResponseRepository surveyResponseRepository) {
        this.questionResponseRepository = questionResponseRepository;
        this.surveyResponseRepository = surveyResponseRepository;
    }
    @Override
    public QuestionResponseDto submitQuestionResponse(QuestionResponseDto questionResponseDto) {
        SurveyResponse surveyResponse = surveyResponseRepository.findById(questionResponseDto.getSurveyResponseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Survey Response Not Found with id " + questionResponseDto.getSurveyResponseId()));

        QuestionResponse entity = QuestionResponseMapper.toEntity(questionResponseDto, surveyResponse);

        // ⚡ Calcul du score de la question en fonction des options sélectionnées
        if (entity.getOptionResponses() != null) {
            long totalOptionScore = entity.getOptionResponses().stream()
                    .filter(OptionResponse::isSelected)
                    .mapToLong(OptionResponse::getOptionScore)
                    .sum();
            entity.setQuestionScore(totalOptionScore);
        }

        return QuestionResponseMapper.toDto(questionResponseRepository.save(entity));
    }

    @Override
    public QuestionResponseDto updateQuestionResponse(UUID id, QuestionResponseDto questionResponseDto) {
        QuestionResponse existing = questionResponseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question Response Not Found with id " + id));

        existing.setSubmittedAt(questionResponseDto.getSubmittedAt());

        if (existing.getOptionResponses() != null) {
            long totalOptionScore = existing.getOptionResponses().stream()
                    .filter(OptionResponse::isSelected)
                    .mapToLong(OptionResponse::getOptionScore)
                    .sum();
            existing.setQuestionScore(totalOptionScore);
        }

        return QuestionResponseMapper.toDto(questionResponseRepository.save(existing));
    }

    @Override
    public QuestionResponseDto getQuestionResponseById(UUID id) {
        QuestionResponse questionResponse = questionResponseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question Response Not Found with id " + id));
        return QuestionResponseMapper.toDto(questionResponse);
    }

    @Override
    public List<QuestionResponseDto> getQuestionResponseBySurveyResponseId(UUID surveyResponseId) {
        List<QuestionResponse> questionResponses = questionResponseRepository.findBySurveyResponseSurveyResponseId(surveyResponseId);
        if (questionResponses.isEmpty()) {
            throw new ResourceNotFoundException("Question Response Not Found with id " + surveyResponseId);
        }
        return questionResponses.stream()
                .map(QuestionResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponseDto> getAllQuestionResponses() {
        return questionResponseRepository.findAll()
                .stream()
                .map(QuestionResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteQuestionResponse(UUID id) {
        QuestionResponse existing = questionResponseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question Response Not Found with id " + id));
        questionResponseRepository.delete(existing);

    }
}
