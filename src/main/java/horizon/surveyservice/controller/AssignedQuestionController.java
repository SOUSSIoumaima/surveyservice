package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.AssignedQuestionDto;
import horizon.surveyservice.service.AssignedQuestionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assigned-question")
public class AssignedQuestionController {
    private final AssignedQuestionService assignedQuestionService;

    public AssignedQuestionController(AssignedQuestionService assignedQuestionService) {
        this.assignedQuestionService = assignedQuestionService;
    }

    @DeleteMapping("/unassign")
    @PreAuthorize("hasAnyAuthority('SURVEY_UPDATE', 'SYS_ADMIN_ROOT')")
    public ResponseEntity<Void> unassignQuestionFromSurvey(
            @RequestParam UUID surveyId,
            @RequestParam UUID questionId) {
        assignedQuestionService.unassignQuestionFromSurvey(surveyId, questionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyAuthority('SURVEY_UPDATE', 'SYS_ADMIN_ROOT')")
    public ResponseEntity<AssignedQuestionDto> assignQuestionToSurvey(
            @RequestParam UUID surveyId,
            @RequestParam UUID questionId,
            @RequestHeader(value = "X-Department-Id", required = false) UUID departmentId,
            @RequestHeader(value = "X-Team-Id", required = false) UUID teamId) {

        AssignedQuestionDto assignedQuestion = assignedQuestionService.assignQuestionToSurvey(
                surveyId, questionId, departmentId, teamId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(assignedQuestion);
    }

    @GetMapping("/survey/{surveyId}")
    @PreAuthorize("hasAnyAuthority('SURVEY_READ', 'SYS_ADMIN_ROOT')")
    public ResponseEntity<List<AssignedQuestionDto>> getAssignedQuestionsBySurvey(@PathVariable UUID surveyId) {
        List<AssignedQuestionDto> assignedQuestions = assignedQuestionService.getAssignedQuestionsBySurvey(surveyId);
        return ResponseEntity.ok(assignedQuestions);
    }

}
