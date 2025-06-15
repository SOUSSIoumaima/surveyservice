package horizon.surveyservice.controller;


import horizon.surveyservice.DTO.QuestionResponseDto;
import horizon.surveyservice.DTO.SurveyResponseDto;
import horizon.surveyservice.service.SurveyResponseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/survey-response")
public class SurveyResponseController {
    private final SurveyResponseService surveyResponseService;
    public SurveyResponseController(SurveyResponseService surveyResponseService) {
        this.surveyResponseService = surveyResponseService;
    }

    @PostMapping
    public ResponseEntity<SurveyResponseDto> submitSurveyResponse(@RequestBody SurveyResponseDto surveyResponseDto){
        List<QuestionResponseDto> questionResponses = surveyResponseDto.getQuestionResponses();

        long totalScore = questionResponses.stream()
                .mapToLong(QuestionResponseDto::getQuestionScore)
                .sum();
        surveyResponseDto.setTotalScore(totalScore);
        SurveyResponseDto createdSurveyResponse = surveyResponseService.submitSurveyResponse(surveyResponseDto);
        return ResponseEntity.ok(createdSurveyResponse);
    }
    @GetMapping
    public ResponseEntity<List<SurveyResponseDto>> getAllSurveyResponses(){
        return ResponseEntity.ok(surveyResponseService.getAllSurveyResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponseDto> getSurveyResponseById(@PathVariable UUID id){
        return ResponseEntity.ok(surveyResponseService.getSurveyResponseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SurveyResponseDto> updateSurveyResponse(@PathVariable UUID id, @RequestBody SurveyResponseDto surveyResponseDto){
        List<QuestionResponseDto> questionResponses = surveyResponseDto.getQuestionResponses();

        long totalScore = questionResponses.stream()
                .mapToLong(QuestionResponseDto::getQuestionScore)
                .sum();
        surveyResponseDto.setTotalScore(totalScore);
        SurveyResponseDto updatedSurveyResponse = surveyResponseService.updateSurveyResponse(id, surveyResponseDto);
        return ResponseEntity.ok(updatedSurveyResponse);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<List<SurveyResponseDto>> deleteSurveyResponse(@PathVariable UUID id){
        surveyResponseService.deleteSurveyResponse(id);
        List<SurveyResponseDto> surveyResponses = surveyResponseService.getAllSurveyResponses();
        return ResponseEntity.ok(surveyResponses);
    }
}

