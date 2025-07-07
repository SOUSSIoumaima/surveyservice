package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.SurveyDto;

import horizon.surveyservice.service.SurveyService;
import horizon.surveyservice.util.JwtUtil;
import horizon.surveyservice.util.OrganizationContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {
    private final SurveyService surveyService;
    private final OrganizationContextUtil orgContextUtil;
    private final JwtUtil jwtUtil;
    public SurveyController(SurveyService surveyService, OrganizationContextUtil orgContextUtil, JwtUtil jwtUtil) {
        this.surveyService = surveyService;
        this.orgContextUtil = orgContextUtil;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping
    @PreAuthorize("hasAnyAuthority('SURVEY_CREATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<SurveyDto> createSurvey(@RequestBody SurveyDto surveyDto) {
        SurveyDto createdSurvey = surveyService.createSurvey(surveyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSurvey);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SURVEY_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<List<SurveyDto>> getAllSurveys() {

        if (orgContextUtil.isRootAdmin()) {
            // ROOT ADMIN voit tout
            return ResponseEntity.ok(surveyService.getAllSurveys());
        } else {
            UUID organizationId = orgContextUtil.getCurrentOrganizationId();
            List<SurveyDto> surveys = surveyService.getSurveysByOrganization(organizationId);
            return ResponseEntity.ok(surveys);
        }
    }

    @GetMapping("/{surveyId}")
    @PreAuthorize("hasAnyAuthority('SURVEY_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<SurveyDto> getSurveyById(@PathVariable UUID surveyId) {
        return ResponseEntity.ok(surveyService.getSurveyById(surveyId));
    }

    @PutMapping("/{surveyId}")
    @PreAuthorize("hasAnyAuthority('SURVEY_UPDATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<SurveyDto> updateSurvey(@PathVariable UUID surveyId, @RequestBody SurveyDto surveyDto) {
        SurveyDto updatedSurvey = surveyService.updateSurvey(surveyId, surveyDto);
        return ResponseEntity.ok(updatedSurvey);
    }
    @DeleteMapping("/{surveyId}")
    @PreAuthorize("hasAnyAuthority('SURVEY_DELETE','SYS_ADMIN_ROOT')")
    public ResponseEntity<List<SurveyDto>> deleteSurvey(@PathVariable UUID surveyId) {
        surveyService.deleteSurvey(surveyId);
        List<SurveyDto> surveys = surveyService.getAllSurveys();
        return ResponseEntity.ok(surveys);
    }

    @PostMapping("/{surveyId}/question/{questionId}")
    @PreAuthorize("hasAnyAuthority('SURVEY_UPDATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<SurveyDto> assignQuestionToSurvey(@PathVariable UUID surveyId,
                                                            @PathVariable UUID questionId,
                                                            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID departmentId = jwtUtil.extractDepartmentId(token);
        UUID teamId = jwtUtil.extractTeamId(token);

        surveyService.assignQuestionToSurvey(surveyId, questionId, departmentId, teamId);
        return ResponseEntity.ok(surveyService.getSurveyById(surveyId));
    }

    @DeleteMapping("/{surveyId}/question/{questionId}")
    @PreAuthorize("hasAnyAuthority('SURVEY_UPDATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<SurveyDto> unassignQuestionFromSurvey(@PathVariable UUID surveyId, @PathVariable UUID questionId) {
        surveyService.unassignQuestionFromSurvey(surveyId, questionId);
        return ResponseEntity.ok(surveyService.getSurveyById(surveyId));
    }

    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasAnyAuthority('SURVEY_LOCK','SYS_ADMIN_ROOT')")
    public ResponseEntity<SurveyDto> lockSurvey(@PathVariable UUID id) {
        return ResponseEntity.ok(surveyService.lockSurvey(id));
    }

    @PatchMapping("/{id}/unlock")
    @PreAuthorize("hasAnyAuthority('SURVEY_UNLOCK','SYS_ADMIN_ROOT')")
    public ResponseEntity<SurveyDto> unlockSurvey(@PathVariable UUID id) {
        return ResponseEntity.ok(surveyService.unlockSurvey(id));
    }
    @GetMapping("/{id}/exists")
    public ResponseEntity< Boolean> exists(@PathVariable UUID id) {
        boolean exists = surveyService.exists(id);
        return ResponseEntity.ok(exists);
    }

}
