package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.service.QuestionService;

import horizon.surveyservice.util.OrganizationContextUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")

public class QuestionController {
    private final QuestionService questionService;
    private final OrganizationContextUtil orgContextUtil;

    public QuestionController(QuestionService questionService, OrganizationContextUtil orgContextUtil) {
        this.questionService = questionService;
        this.orgContextUtil = orgContextUtil;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('QUESTION_CREATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<QuestionDto> createQuestion(@RequestBody QuestionDto questionDto) {
        QuestionDto created = questionService.createQuestion(questionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('QUESTION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        if (orgContextUtil.isRootAdmin()) {
            return ResponseEntity.ok(questionService.getAllQuestions());
        } else {
            UUID organizationId = orgContextUtil.getCurrentOrganizationId();
            List<QuestionDto> questions = questionService.getQuestionByOrganization(organizationId);
            return ResponseEntity.ok(questions);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('QUESTION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable UUID id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('QUESTION_UPDATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<QuestionDto> updateQuestion(@PathVariable UUID id, @RequestBody QuestionDto questionDto) {
        QuestionDto updated = questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('QUESTION_DELETE','SYS_ADMIN_ROOT')")
    public ResponseEntity<List<QuestionDto>> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/subject/{subject}")
    @PreAuthorize("hasAnyAuthority('QUESTION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<List<QuestionDto>> getBySubject(@PathVariable String subject) {
        if (orgContextUtil.isRootAdmin()) {
            return ResponseEntity.ok(questionService.getBySubject(subject));
        } else {
            UUID orgId = orgContextUtil.getCurrentOrganizationId();
            return ResponseEntity.ok(questionService.getBySubjectAndOrganization(subject, orgId));
        }
    }

    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasAnyAuthority('QUESTION_LOCK','SYS_ADMIN_ROOT')")
    public ResponseEntity<QuestionDto> lockQuestion(@PathVariable UUID id) {
        QuestionDto locked = questionService.lockQuestion(id);
        return ResponseEntity.ok(locked);
    }

    @PatchMapping("/{id}/unlock")
    @PreAuthorize("hasAnyAuthority('QUESTION_UNLOCK','SYS_ADMIN_ROOT')")
    public ResponseEntity<QuestionDto> unlockQuestion(@PathVariable UUID id) {
        QuestionDto unlocked = questionService.unlockQuestion(id);
        return ResponseEntity.ok(unlocked);
    }
}