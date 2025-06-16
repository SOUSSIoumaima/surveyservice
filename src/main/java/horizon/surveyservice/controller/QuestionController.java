package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
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
        this.orgContextUtil = orgContextUtil;
        this.questionService = questionService;
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
        if (orgContextUtil.isRootAdmin()){
            return ResponseEntity.ok(questionService.getAllQuestions());
        }else {
            UUID organizationId= orgContextUtil.getCurrentOrganizationId();
            List<QuestionDto> questions = questionService.getQuestionByOrganization(organizationId);
            return ResponseEntity.ok(questions);
        }
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('QUESTION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable UUID id) {
        QuestionDto question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('QUESTION_UPDATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> updateQuestion(@PathVariable UUID id, @RequestBody QuestionDto questionDto) {
        QuestionDto updated = questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('QUESTION_DELETE','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        List<QuestionDto> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);

    }
    @GetMapping("/subject/{subject}")
    @PreAuthorize("hasAnyAuthority('QUESTION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<List<QuestionDto>> getBySubject(@PathVariable String subject) {
        return ResponseEntity.ok(questionService.getBySubject(subject));
    }
    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasAnyAuthority('QUESTION_LOCK','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> lockQuestion(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(questionService.lockQuestion(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
        }
    }

    @PatchMapping("/{id}/unlock")
    @PreAuthorize("hasAnyAuthority('QUESTION_UNLOCK','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> unlockQuestion(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(questionService.unlockQuestion(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
        }
    }


}
