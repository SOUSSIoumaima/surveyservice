package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.SurveyDto;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.entity.Survey;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.exeptions.LockedException;
import horizon.surveyservice.mapper.SurveyMapper;
import horizon.surveyservice.repository.QuestionRepository;
import horizon.surveyservice.repository.SurveyRepository;
import horizon.surveyservice.service.SurveyService;
import horizon.surveyservice.util.OrganizationContextUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final OrganizationContextUtil organizationContextUtil;


    public SurveyServiceImpl(SurveyRepository surveyRepository, QuestionRepository questionRepository, OrganizationContextUtil organizationContextUtil) {
        this.organizationContextUtil = organizationContextUtil;
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
    }
    @Override
    public SurveyDto createSurvey(SurveyDto surveyDto) {
        UUID currentOrgId = organizationContextUtil.getCurrentOrganizationId();
        surveyDto.setOrganizationId(currentOrgId);
        UUID currentUserId = organizationContextUtil.getCurrentUserId();
        surveyDto.setOwnerId(currentUserId);
        Survey survey = SurveyMapper.toSurveyEntity(surveyDto);
        Survey saved=surveyRepository.save(survey);
        return SurveyMapper.toSurveyDto(saved);
    }

    @Override
    public List<SurveyDto> getAllSurveys() {
        List<Survey> surveys = surveyRepository.findAll();
        return surveys.stream()
                .filter(s -> {
                    try {
                        organizationContextUtil.validateOrganizationAccess(s.getOrganizationId());
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(SurveyMapper::toSurveyDto)
                .collect(Collectors.toList());

    }

    @Override
    public SurveyDto getSurveyById(UUID surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(()-> new ResourceNotFoundException("Survey not found with id " + surveyId));
        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());
        return SurveyMapper.toSurveyDto(survey);
    }

    @Override
    public SurveyDto updateSurvey(UUID surveyId, SurveyDto surveyDto) {
        Survey existing = surveyRepository.findById(surveyId)
                .orElseThrow(()-> new ResourceNotFoundException("Survey not found with id " + surveyId));
        organizationContextUtil.validateOrganizationAccess(existing.getOrganizationId());

        if (existing.isLocked()){
            throw new LockedException("Survey is locked cannot be updated");
        }
        existing.setTitle(surveyDto.getTitle());
        existing.setDescription(surveyDto.getDescription());
        existing.setStatus(surveyDto.getStatus());
        existing.setDeadline(surveyDto.getDeadline());
        existing.setType(surveyDto.getType());
        Survey updated=surveyRepository.save(existing);
        return SurveyMapper.toSurveyDto(updated);
    }

    @Override
    public void deleteSurvey(UUID surveyId) {
        Survey existing = surveyRepository.findById(surveyId)
                .orElseThrow(()-> new ResourceNotFoundException("Survey not found with id " + surveyId));
        organizationContextUtil.validateOrganizationAccess(existing.getOrganizationId());

        if (existing.isLocked()) {
            throw new LockedException("Survey is locked and cannot be deleted");
        }
        surveyRepository.delete(existing);

    }

    @Override
    public void assignQuestionToSurvey(UUID surveyId, UUID questionId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id " + surveyId));
        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());

        if (survey.isLocked()) {
            throw new LockedException("Survey is locked and cannot be modified");
        }
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
        if (question.isLocked()) {
            throw new LockedException("Question is locked and cannot be modified");
        }
        List<Question> questions = survey.getQuestions();
        if (!questions.contains(question)) {
            questions.add(question);
            survey.setQuestions(questions);
            surveyRepository.save(survey);
        }
    }

    @Override
    public void unassignQuestionFromSurvey(UUID surveyId, UUID questionId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id " + surveyId));
        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());
        if (survey.isLocked()) {
            throw new LockedException("Survey is locked and cannot be modified");
        }
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
        if (question.isLocked()) {
            throw new LockedException("Question is locked and cannot be modified");
        }

        List<Question> questions = survey.getQuestions();
        if (questions.contains(question)) {
            questions.remove(question);
            survey.setQuestions(questions);
            surveyRepository.save(survey);
        }

    }
    @Override
    public List<SurveyDto> getSurveysByOrganization(UUID organizationId) {
        List<Survey> surveys = surveyRepository.findByOrganizationId(organizationId);
        return surveys.stream()
                .map(SurveyMapper::toSurveyDto)
                .collect(Collectors.toList());
    }


    @Override
    public SurveyDto lockSurvey(UUID id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Survey not found with id " + id));
        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());

        survey.setLocked(true);
        surveyRepository.save(survey);
        return SurveyMapper.toSurveyDto(survey);
    }

    @Override
    public SurveyDto unlockSurvey(UUID id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Survey not found with id " + id));
        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());

        survey.setLocked(false);
        surveyRepository.save(survey);
        return SurveyMapper.toSurveyDto(survey);
    }
    public boolean exists(UUID id) {
        return surveyRepository.existsById(id);
    }
}
