package horizon.surveyservice.service;

import horizon.surveyservice.DTO.OptionDto;
import java.util.List;
import java.util.UUID;

public interface OptionService {
    OptionDto createOption(OptionDto optionDto);
    OptionDto updateOption(UUID id, OptionDto optionDto);
    OptionDto getOptionById(UUID id);
    List<OptionDto> getOptionByQuestionId(UUID questionId);
    List<OptionDto> getAllOptions();
    void deleteOption(UUID id);
    OptionDto lockOption(UUID id);
    OptionDto unlockOption(UUID id);
}
