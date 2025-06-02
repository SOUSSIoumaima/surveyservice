package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.exeptions.LockedException;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.mapper.QuestionMapper;
import horizon.surveyservice.repository.QuestionRepository;
import horizon.surveyservice.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {

        this.questionRepository = questionRepository;
    }
    @Override
    public QuestionDto createQuestion(QuestionDto questionDto) {
        Question question = QuestionMapper.toEntity(questionDto);
        Question saved = questionRepository.save(question);
        return QuestionMapper.toDTO(saved);
    }

    @Override
    public List<QuestionDto> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(QuestionMapper::toDTO)
                .collect(Collectors.toList());

    }

    @Override
    public QuestionDto getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:"+id));
        return QuestionMapper.toDTO(question);

    }



    @Override
    public QuestionDto updateQuestion(Long id,QuestionDto questionDto) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:"+id));
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
        List<Question> question = questionRepository.findBySubject(subject);
        if (question.isEmpty()) {
            throw new ResourceNotFoundException("Question not found with subject:"+subject);
        }
        return question.stream()
                .map(QuestionMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteQuestion(Long id) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:" + id));
        if (existing.isLocked()) {
            throw new LockedException("Cannot delete a locked question with id: " + id);
        }
        questionRepository.deleteById(id);

    }

    @Override
    public QuestionDto lockQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:"+id));
        question.setLocked(true);
        Question saved = questionRepository.save(question);
        return QuestionMapper.toDTO(saved);
    }

    @Override
    public QuestionDto unlockQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:"+id));
        question.setLocked(false);
        Question saved = questionRepository.save(question);
        return QuestionMapper.toDTO(saved);
    }
}
