package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.OptionResponseDto;
import horizon.surveyservice.entity.OptionResponse;
import horizon.surveyservice.entity.QuestionResponse;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.mapper.OptionResponseMapper;
import horizon.surveyservice.repository.OptionResponseRepository;
import horizon.surveyservice.repository.QuestionResponseRepository;
import horizon.surveyservice.service.OptionResponseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class OptionResponseServiceImpl implements OptionResponseService {
    private final OptionResponseRepository optionResponseRepository;
    private final QuestionResponseRepository questionResponseRepository;

    public OptionResponseServiceImpl(OptionResponseRepository optionResponseRepository, QuestionResponseRepository questionResponseRepository) {
        this.optionResponseRepository = optionResponseRepository;
        this.questionResponseRepository = questionResponseRepository;
    }

    @Override
    public OptionResponseDto submitOptionResponse(OptionResponseDto dto) {
        QuestionResponse questionResponse = questionResponseRepository.findById(dto.getQuestionResponseId())
                .orElseThrow(() -> new ResourceNotFoundException("QuestionResponse not found with id " + dto.getQuestionResponseId()));
        OptionResponse entity = OptionResponseMapper.toEntity(dto, questionResponse);
        return OptionResponseMapper.toDto(optionResponseRepository.save(entity));
    }

    @Override
    public OptionResponseDto updateOptionResponse(Long id, OptionResponseDto optionResponseDto) {
        OptionResponse existing = optionResponseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OptionResponse not found with id " + id));
        existing.setSelected(optionResponseDto.isSelected());

        OptionResponse updated = optionResponseRepository.save(existing);
        return OptionResponseMapper.toDto(updated);
    }

    @Override
    public OptionResponseDto getOptionResponseById(Long id) {
        OptionResponse optionResponse = optionResponseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OptionResponse not found with id " + id));
        return OptionResponseMapper.toDto(optionResponse);
    }

    @Override
    public List<OptionResponseDto> getOptionResponseByQuestionResponseId(Long questionResponseId) {
        List<OptionResponse> optionResponses = optionResponseRepository.findByQuestionResponseQuestionResponseId(questionResponseId);
        if (optionResponses.isEmpty()) {
            throw new ResourceNotFoundException("OptionResponse not found with id " + questionResponseId);
        }
        return optionResponses.stream()
                .map(OptionResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OptionResponseDto> getAllOptionResponses() {
        return optionResponseRepository.findAll().stream()
                .map(OptionResponseMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public void deleteOptionResponse(Long id) {
        OptionResponse existing = optionResponseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OptionResponse not found with id " + id));
        optionResponseRepository.delete(existing);
    }
}
