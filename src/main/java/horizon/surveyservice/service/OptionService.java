package horizon.surveyservice.service;

import horizon.surveyservice.DTO.OptionDto;

import java.util.List;

public interface OptionService {
    OptionDto createOption(OptionDto optionDto);
    OptionDto updateOption(Long id,OptionDto optionDto);
    OptionDto getOptionById(Long id);
    List<OptionDto> getOptionByQuestionId(Long questionId);
    List<OptionDto> getAllOptions();
    void deleteOption(Long id);
    OptionDto lockOption(Long id);
    OptionDto unlockOption(Long id);
}
