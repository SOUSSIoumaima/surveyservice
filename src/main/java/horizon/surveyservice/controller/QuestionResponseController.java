package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.OptionResponseDto;
import horizon.surveyservice.DTO.QuestionResponseDto;
import horizon.surveyservice.service.QuestionResponseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/question-response")
public class QuestionResponseController {
    private final QuestionResponseService questionResponseService;
    public QuestionResponseController(QuestionResponseService questionResponseService) {
        this.questionResponseService = questionResponseService;
    }
    @PostMapping
    public ResponseEntity<QuestionResponseDto> submitQuestionResponse (@RequestBody QuestionResponseDto questionResponseDto) {
        long questionScore = questionResponseDto.getOptionResponses().stream()
                .filter(OptionResponseDto::isSelected)
                .mapToLong(OptionResponseDto::getOptionScore)
                .sum();
        questionResponseDto.setQuestionScore(questionScore);
        QuestionResponseDto responseDto = questionResponseService.submitQuestionResponse(questionResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getAllQuestionResponses () {
        return ResponseEntity.ok(questionResponseService.getAllQuestionResponses());
    }
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionResponseById (@PathVariable UUID id) {
        QuestionResponseDto responseDto = questionResponseService.getQuestionResponseById(id);
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/bySurvey/{surveyResponseId}")
    public ResponseEntity<?> getQuestionResponseBySurveyResponseId (@PathVariable UUID surveyResponseId) {
        List<QuestionResponseDto> responses = questionResponseService.getQuestionResponseBySurveyResponseId(surveyResponseId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestionResponse (@PathVariable UUID id, @RequestBody QuestionResponseDto questionResponseDto) {
        long questionScore = questionResponseDto.getOptionResponses().stream()
                .filter(OptionResponseDto::isSelected)
                .mapToLong(OptionResponseDto::getOptionScore)
                .sum();
        questionResponseDto.setQuestionScore(questionScore);
        QuestionResponseDto response = questionResponseService.updateQuestionResponse(id, questionResponseDto);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestionResponse(@PathVariable UUID id) {
        questionResponseService.deleteQuestionResponse(id);
        List<QuestionResponseDto> responses = questionResponseService.getAllQuestionResponses();
        return ResponseEntity.ok(responses);
    }


}
