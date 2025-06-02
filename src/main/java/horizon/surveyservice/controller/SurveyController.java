package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.SurveyDto;

import horizon.surveyservice.service.SurveyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {
    private final SurveyService surveyService;
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }
    @PostMapping
    public ResponseEntity<SurveyDto> createSurvey(@RequestBody SurveyDto surveyDto) {
        SurveyDto createdSurvey = surveyService.createSurvey(surveyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSurvey);
    }

    @GetMapping
    public ResponseEntity<List<SurveyDto>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurveys());
    }

    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyDto> getSurveyById(@PathVariable Long surveyId) {
        return ResponseEntity.ok(surveyService.getSurveyById(surveyId));
    }

    @PutMapping("/{surveyId}")
    public ResponseEntity<SurveyDto> updateSurvey(@PathVariable Long surveyId, @RequestBody SurveyDto surveyDto) {
        SurveyDto updatedSurvey = surveyService.updateSurvey(surveyId, surveyDto);
        return ResponseEntity.ok(updatedSurvey);
    }
    @DeleteMapping("/{surveyId}")
    public ResponseEntity<List<SurveyDto>> deleteSurvey(@PathVariable Long surveyId) {
        surveyService.deleteSurvey(surveyId);
        List<SurveyDto> surveys = surveyService.getAllSurveys();
        return ResponseEntity.ok(surveys);
    }

    @PostMapping("/{surveyId}/question/{questionId}")
    public ResponseEntity<SurveyDto> assignQuestionToSurvey(@PathVariable Long surveyId, @PathVariable Long questionId) {
        surveyService.assignQuestionToSurvey(surveyId, questionId);
        return ResponseEntity.ok(surveyService.getSurveyById(surveyId));
    }

    @DeleteMapping("/{surveyId}/question/{questionId}")
    public ResponseEntity<SurveyDto> unassignQuestionFromSurvey(@PathVariable Long surveyId, @PathVariable Long questionId) {
        surveyService.unassignQuestionFromSurvey(surveyId, questionId);
        return ResponseEntity.ok(surveyService.getSurveyById(surveyId));
    }

    @PatchMapping("/{id}/lock")
    public ResponseEntity<SurveyDto> lockSurvey(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.lockSurvey(id));
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<SurveyDto> unlockSurvey(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.unlockSurvey(id));
    }

}
