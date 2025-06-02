package horizon.surveyservice.controller;


import horizon.surveyservice.DTO.SurveyResponseDto;
import horizon.surveyservice.service.SurveyResponseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/survey-response")
public class SurveyResponseController {
    private final SurveyResponseService surveyResponseService;
    public SurveyResponseController(SurveyResponseService surveyResponseService) {
        this.surveyResponseService = surveyResponseService;
    }

    @PostMapping
    public ResponseEntity<SurveyResponseDto> submitSurveyResponse(@RequestBody SurveyResponseDto surveyResponseDto){
        SurveyResponseDto createdSurveyResponse = surveyResponseService.submitSurveyResponse(surveyResponseDto);
        return ResponseEntity.ok(createdSurveyResponse);
    }
    @GetMapping
    public ResponseEntity<List<SurveyResponseDto>> getAllSurveyResponses(){
        return ResponseEntity.ok(surveyResponseService.getAllSurveyResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponseDto> getSurveyResponseById(@PathVariable Long id){
        return ResponseEntity.ok(surveyResponseService.getSurveyResponseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SurveyResponseDto> updateSurveyResponse(@PathVariable Long id, @RequestBody SurveyResponseDto surveyResponseDto){
        SurveyResponseDto updatedSurveyResponse = surveyResponseService.updateSurveyResponse(id, surveyResponseDto);
        return ResponseEntity.ok(updatedSurveyResponse);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<List<SurveyResponseDto>> deleteSurveyResponse(@PathVariable Long id){
        surveyResponseService.deleteSurveyResponse(id);
        List<SurveyResponseDto> surveyResponses = surveyResponseService.getAllSurveyResponses();
        return ResponseEntity.ok(surveyResponses);
    }
}

