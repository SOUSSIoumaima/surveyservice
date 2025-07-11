package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.exeptions.LockedException;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.mapper.QuestionMapper;
import horizon.surveyservice.repository.QuestionRepository;
import horizon.surveyservice.service.QuestionService;
import horizon.surveyservice.util.OrganizationContextUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final OrganizationContextUtil organizationContextUtil;

    public QuestionServiceImpl(QuestionRepository questionRepository, OrganizationContextUtil organizationContextUtil) {
        this.organizationContextUtil = organizationContextUtil;
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
    public QuestionDto updateQuestion(UUID id,QuestionDto questionDto) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:"+id));
        organizationContextUtil.validateOrganizationAccess(existing.getOrganizationId());
        if (existing.isLocked()){
            throw new LockedException("Question is locked cannot be updated");
        }
        existing.setSubject(questionDto.getSubject());
        existing.setQuestionText(questionDto.getQuestionText());
        existing.setQuestionType(questionDto.getQuestionType());
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
