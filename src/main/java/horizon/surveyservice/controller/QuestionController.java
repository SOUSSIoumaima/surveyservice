package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.service.QuestionService;

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
    public QuestionController(QuestionService questionService) {

        this.questionService = questionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('PERMISSION_CREATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<QuestionDto> createQuestion(@RequestBody QuestionDto questionDto) {
        QuestionDto created = questionService.createQuestion(questionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PERMISSION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PERMISSION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable UUID id) {
        QuestionDto question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PERMISSION_UPDATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> updateQuestion(@PathVariable UUID id, @RequestBody QuestionDto questionDto) {
        QuestionDto updated = questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PERMISSION_DELETE','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        List<QuestionDto> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);

    }
    @GetMapping("/subject/{subject}")
    @PreAuthorize("hasAnyAuthority('PERMISSION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<List<QuestionDto>> getBySubject(@PathVariable String subject) {
        return ResponseEntity.ok(questionService.getBySubject(subject));
    }
    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasAnyAuthority('PERMISSION_UPDATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> lockQuestion(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(questionService.lockQuestion(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
        }
    }

    @PatchMapping("/{id}/unlock")
    @PreAuthorize("hasAnyAuthority('PERMISSION_UPDATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> unlockQuestion(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(questionService.unlockQuestion(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
        }
    }


}
