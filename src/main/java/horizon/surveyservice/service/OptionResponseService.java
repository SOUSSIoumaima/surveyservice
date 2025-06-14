package horizon.surveyservice.service;


import horizon.surveyservice.DTO.OptionResponseDto;


import java.util.List;
import java.util.UUID;

public interface OptionResponseService {
    OptionResponseDto submitOptionResponse(OptionResponseDto optionResponseDto);
    OptionResponseDto updateOptionResponse(UUID id, OptionResponseDto optionResponseDto);
    OptionResponseDto getOptionResponseById(UUID id);
    List<OptionResponseDto> getOptionResponseByQuestionResponseId(UUID questionResponseId);
    List<OptionResponseDto> getAllOptionResponses();
    void deleteOptionResponse(UUID id);
}
