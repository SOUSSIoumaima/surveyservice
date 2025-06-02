package horizon.surveyservice.service.serviceimpl;


import horizon.surveyservice.DTO.OptionDto;
import horizon.surveyservice.entity.Option;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.exeptions.LockedException;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.mapper.OptionMapper;
import horizon.surveyservice.repository.OptionRepository;
import horizon.surveyservice.repository.QuestionRepository;
import horizon.surveyservice.service.OptionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class OptionServiceImpl implements OptionService {
    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;

    public OptionServiceImpl(OptionRepository optionRepository, QuestionRepository questionRepository) {
        this.optionRepository = optionRepository;
        this.questionRepository = questionRepository;
    }
    @Override
    public OptionDto createOption(OptionDto optionDto) {
        Question question = questionRepository.findById(optionDto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found with id:"+ optionDto.getQuestionId()));
        Option option = OptionMapper.toEntity(optionDto,question);
        Option saved = optionRepository.save(option);
        return OptionMapper.toDto(saved);
    }

    @Override
    public OptionDto updateOption(Long id, OptionDto optionDto) {
        Option existing = optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id:"+ id));
        if (existing.isLocked()) {
            throw new LockedException("Option is locked cannot be updated");
        }
        Question question = questionRepository.findById(optionDto.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id:"+ optionDto.getQuestionId()));
        existing.setOptionText(optionDto.getOptionText());
        existing.setCorrect(optionDto.isCorrect());
        existing.setOptionScore(optionDto.getOptionScore());
        existing.setLocked(optionDto.isLocked());
        existing.setQuestion(question);
        Option updated = optionRepository.save(existing);
        return OptionMapper.toDto(updated);
    }

    @Override
    public OptionDto getOptionById(Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id:"+ id));
        return OptionMapper.toDto(option);
    }

    @Override
    public List<OptionDto> getOptionByQuestionId(Long questionId) {
        List<Option> option = optionRepository.findByQuestionQuestionId(questionId);
        if (option.isEmpty()) {
            throw new ResourceNotFoundException("Option not found with Question id :"+questionId);
        }
        return option.stream()
                .map(OptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OptionDto> getAllOptions() {
        return optionRepository.findAll()
                .stream().map(OptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOption(Long id) {
        Option existing = optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + id));
        if (existing.isLocked()) {
            throw new LockedException("Option is locked cannot be deleted");
        }
        optionRepository.delete(existing);

    }

    @Override
    public OptionDto lockOption(Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + id));
        option.setLocked(true);
        optionRepository.save(option);
        return OptionMapper.toDto(option);
    }

    @Override
    public OptionDto unlockOption(Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + id));
        option.setLocked(false);
        optionRepository.save(option);
        return OptionMapper.toDto(option);
    }
}
