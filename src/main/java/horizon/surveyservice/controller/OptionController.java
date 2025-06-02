package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.OptionDto;
import horizon.surveyservice.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")

public class OptionController {
    @Autowired
    private final OptionService optionService;
    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PreAuthorize("hasAnyAuthority('PERMISSION_CREATE','ADMIN_ROOT')")
    @PostMapping
    public ResponseEntity<OptionDto> createOption(@RequestBody OptionDto optionDto) {
        return ResponseEntity.ok(optionService.createOption(optionDto));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOptionById(@PathVariable Long id) {
            OptionDto optionDto = optionService.getOptionById(id);
            return ResponseEntity.ok(optionDto);
    }

    @GetMapping("/byQuestion/{questionId}")
    public ResponseEntity<?> getOptionByQuestionId(@PathVariable Long questionId) {
        List<OptionDto> options = optionService.getOptionByQuestionId(questionId);
        return ResponseEntity.ok(options);
    }

    @GetMapping
    public ResponseEntity<List<OptionDto>> getAllOptions() {
        return ResponseEntity.ok(optionService.getAllOptions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOption(@PathVariable Long id, @RequestBody OptionDto optionDto) {
        OptionDto updatedOption = optionService.updateOption(id, optionDto);
        return ResponseEntity.ok(updatedOption);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<OptionDto>> deleteOption(@PathVariable Long id) {
        optionService.deleteOption(id);
        List<OptionDto> options = optionService.getAllOptions();
        return ResponseEntity.ok(options);

    }
    @PatchMapping("/{id}/lock")
    public ResponseEntity<OptionDto> lockOption(@PathVariable Long id) {
        return ResponseEntity.ok(optionService.lockOption(id));
    }
    @PatchMapping("/{id}/unlock")
    public ResponseEntity<OptionDto> unlockOption(@PathVariable Long id) {
        return ResponseEntity.ok(optionService.unlockOption(id));
    }


}
