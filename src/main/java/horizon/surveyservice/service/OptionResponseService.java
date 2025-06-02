package horizon.surveyservice.service;


import horizon.surveyservice.DTO.OptionResponseDto;


import java.util.List;

public interface OptionResponseService {
    OptionResponseDto submitOptionResponse(OptionResponseDto optionResponseDto);
    OptionResponseDto updateOptionResponse(Long id,OptionResponseDto optionResponseDto);
    OptionResponseDto getOptionResponseById(Long id);
    List<OptionResponseDto> getOptionResponseByQuestionResponseId(Long questionResponseId);
    List<OptionResponseDto> getAllOptionResponses();
    void deleteOptionResponse(Long id);
}
