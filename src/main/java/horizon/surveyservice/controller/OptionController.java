package horizon.surveyservice.controller;

import horizon.surveyservice.DTO.OptionDto;
import horizon.surveyservice.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/options")

public class OptionController {
    @Autowired
    private final OptionService optionService;
    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('OPTION_CREATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<OptionDto> createOption(@RequestBody OptionDto optionDto) {
        return ResponseEntity.ok(optionService.createOption(optionDto));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OPTION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> getOptionById(@PathVariable UUID id) {
            OptionDto optionDto = optionService.getOptionById(id);
            return ResponseEntity.ok(optionDto);
    }

    @GetMapping("/byQuestion/{questionId}")
    @PreAuthorize("hasAnyAuthority('OPTION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> getOptionByQuestionId(@PathVariable UUID questionId) {
        List<OptionDto> options = optionService.getOptionByQuestionId(questionId);
        return ResponseEntity.ok(options);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('OPTION_READ','SYS_ADMIN_ROOT')")
    public ResponseEntity<List<OptionDto>> getAllOptions() {
        return ResponseEntity.ok(optionService.getAllOptions());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OPTION_UPDATE','SYS_ADMIN_ROOT')")
    public ResponseEntity<?> updateOption(@PathVariable UUID id, @RequestBody OptionDto optionDto) {
        OptionDto updatedOption = optionService.updateOption(id, optionDto);
        return ResponseEntity.ok(updatedOption);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OPTION_DELETE','SYS_ADMIN_ROOT')")
    public ResponseEntity<List<OptionDto>> deleteOption(@PathVariable UUID id) {
        optionService.deleteOption(id);
        List<OptionDto> options = optionService.getAllOptions();
        return ResponseEntity.ok(options);

    }
    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasAnyAuthority('OPTION_LOCK','SYS_ADMIN_ROOT')")
    public ResponseEntity<OptionDto> lockOption(@PathVariable UUID id) {
        return ResponseEntity.ok(optionService.lockOption(id));
    }
    @PatchMapping("/{id}/unlock")
    @PreAuthorize("hasAnyAuthority('OPTION_UNLOCK','SYS_ADMIN_ROOT')")
    public ResponseEntity<OptionDto> unlockOption(@PathVariable UUID id) {
        return ResponseEntity.ok(optionService.unlockOption(id));
    }


}
