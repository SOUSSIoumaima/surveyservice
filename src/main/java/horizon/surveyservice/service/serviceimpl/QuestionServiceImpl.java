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
import horizon.surveyservice.service.QuestionService;
import horizon.surveyservice.util.OrganizationContextUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final OrganizationContextUtil organizationContextUtil;

    public QuestionServiceImpl(QuestionRepository questionRepository, OrganizationContextUtil organizationContextUtil, OptionRepository optionRepository) {
        this.organizationContextUtil = organizationContextUtil;
        this.optionRepository = optionRepository;
        this.questionRepository = questionRepository;
    }
    @Override
    public QuestionDto createQuestion(QuestionDto questionDto) {
        UUID currentOrgId = organizationContextUtil.getCurrentOrganizationId();
        questionDto.setOrganizationId(currentOrgId);
        Question question = QuestionMapper.toEntity(questionDto);
        Question saved = questionRepository.save(question);
        return QuestionMapper.toDTO(saved);
    }

    @Override
    public List<QuestionDto> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        return questions.stream()
                .filter(q->{
                    try{
                        organizationContextUtil.validateOrganizationAccess(q.getOrganizationId());
                        return true;
                    }catch (Exception e){
                        return false;
                    }
                }).map(QuestionMapper::toDTO).collect(Collectors.toList());


    }

    @Override
    public QuestionDto getQuestionById(UUID id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:"+id));
        organizationContextUtil.validateOrganizationAccess(question.getOrganizationId());
        return QuestionMapper.toDTO(question);

    }


    @Override
    @Transactional
    public QuestionDto updateQuestion(UUID id, QuestionDto questionDto) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:" + id));

        organizationContextUtil.validateOrganizationAccess(existing.getOrganizationId());

        if (existing.isLocked()) {
            throw new LockedException("Question is locked and cannot be updated");
        }

        existing.setSubject(questionDto.getSubject());
        existing.setQuestionText(questionDto.getQuestionText());
        existing.setQuestionType(questionDto.getQuestionType());

        List<OptionDto> incomingOptions = questionDto.getOptions();

        // Créer une nouvelle liste d'options synchronisées
        List<Option> updatedOptions = incomingOptions.stream().map(optDto -> {
            Option opt;
            if (optDto.getOptionId() != null) {
                // Option existante à mettre à jour
                opt = optionRepository.findById(optDto.getOptionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Option not found with id:" + optDto.getOptionId()));
                if (opt.isLocked()) {
                    throw new LockedException("Option is locked and cannot be updated: " + optDto.getOptionId());
                }
            } else {
                // Nouvelle option à créer
                opt = new Option();
                opt.setQuestion(existing);
            }
            opt.setOptionText(optDto.getOptionText());
            opt.setCorrect(optDto.isCorrect());
            opt.setOptionScore(optDto.getOptionScore());
            opt.setLocked(optDto.isLocked());
            return opt;
        }).collect(Collectors.toList());

        // Remplacer la liste des options dans la question
        existing.setOptions(updatedOptions);

        // Sauvegarder la question (et les options synchronisées grâce à cascade et orphanRemoval)
        Question updated = questionRepository.save(existing);

        return QuestionMapper.toDTO(updated);
    }

    @Override
    public List<QuestionDto> getBySubject(String subject) {
        List<Question> questions = questionRepository.findBySubjectContainingIgnoreCase(subject);
        return questions.stream()
                .filter(q -> {
                    try {
                        organizationContextUtil.validateOrganizationAccess(q.getOrganizationId());
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(QuestionMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteQuestion(UUID id) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:" + id));
        organizationContextUtil.validateOrganizationAccess(existing.getOrganizationId());
        if (existing.isLocked()) {
            throw new LockedException("Cannot delete a locked question with id: " + id);
        }
        questionRepository.deleteById(id);

    }

    @Override
    public QuestionDto lockQuestion(UUID id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:"+id));
        organizationContextUtil.validateOrganizationAccess(question.getOrganizationId());
        question.setLocked(true);
        Question saved = questionRepository.save(question);
        return QuestionMapper.toDTO(saved);
    }

    @Override
    public QuestionDto unlockQuestion(UUID id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:"+id));
        organizationContextUtil.validateOrganizationAccess(question.getOrganizationId());
        question.setLocked(false);
        Question saved = questionRepository.save(question);
        return QuestionMapper.toDTO(saved);
    }

    @Override
    public List<QuestionDto> getQuestionByOrganization(UUID organizationId) {
        List<Question> questions = questionRepository.findByOrganizationId(organizationId);
        return questions.stream()
                .map(QuestionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionDto> getBySubjectAndOrganization(String subject, UUID organizationId) {
        List<Question> questions = questionRepository.findBySubjectContainingIgnoreCaseAndOrganizationId(subject, organizationId);
        return questions.stream()
                .map(QuestionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
