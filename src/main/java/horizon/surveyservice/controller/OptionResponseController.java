package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.OptionResponseDto;
import horizon.surveyservice.service.OptionResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/option-responses")
public class OptionResponseController {
    private final OptionResponseService optionResponseService;
    @Autowired
    public OptionResponseController(OptionResponseService optionResponseService) {
    this.optionResponseService = optionResponseService;}
    @PostMapping
    public ResponseEntity<OptionResponseDto> submitOptionResponse(@RequestBody OptionResponseDto optionResponseDto) {
        return ResponseEntity.ok(optionResponseService.submitOptionResponse(optionResponseDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOptionResponseById(@PathVariable Long id) {
        OptionResponseDto optionResponseDto = optionResponseService.getOptionResponseById(id);
        return ResponseEntity.ok(optionResponseDto);
    }
    @GetMapping("/byQuestion/{questionResponseId}")
    public ResponseEntity<?> getOptionResponseByQuestionResponseId(@PathVariable Long questionResponseId) {
        List<OptionResponseDto> optionResponses = optionResponseService.getOptionResponseByQuestionResponseId(questionResponseId);
        return ResponseEntity.ok(optionResponses);
    }
    @GetMapping
    public ResponseEntity<?> getAllOptionResponses() {
        List<OptionResponseDto> optionResponses = optionResponseService.getAllOptionResponses();
        return ResponseEntity.ok(optionResponses);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOptionResponse(@PathVariable Long id, @RequestBody OptionResponseDto optionResponseDto) {
        OptionResponseDto updatedOptionResponseDto = optionResponseService.updateOptionResponse(id, optionResponseDto);
        return ResponseEntity.ok(updatedOptionResponseDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOptionResponse(@PathVariable Long id) {
        optionResponseService.deleteOptionResponse(id);
        List<OptionResponseDto> optionResponses = optionResponseService.getAllOptionResponses();
        return ResponseEntity.ok(optionResponses);
    }
}
