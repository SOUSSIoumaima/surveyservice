package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.AssignedQuestionDto;
import horizon.surveyservice.service.AssignedQuestionService;
import horizon.surveyservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtUtil jwtUtil;
    public AssignedQuestionController(AssignedQuestionService assignedQuestionService, JwtUtil jwtUtil) {
        this.assignedQuestionService = assignedQuestionService;
        this.jwtUtil = jwtUtil;

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
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7); // enlève "Bearer "

        UUID departmentId = jwtUtil.extractDepartmentId(token);
        UUID teamId = jwtUtil.extractTeamId(token);

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
