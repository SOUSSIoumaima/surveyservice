package horizon.surveyservice.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import horizon.surveyservice.DTO.OptionDto;
import horizon.surveyservice.service.OptionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OptionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OptionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OptionService optionService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OptionService optionService() {
            return Mockito.mock(OptionService.class);
        }
    }

    @Test
    void testCreateOption() throws Exception {
        OptionDto request = new OptionDto(null, UUID.randomUUID(), "Option A", 10L, true, false);
        OptionDto response = new OptionDto(UUID.randomUUID(), request.getQuestionId(), "Option A", 10L, true, false);

        Mockito.when(optionService.createOption(any(OptionDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionId").value(response.getOptionId().toString()))
                .andExpect(jsonPath("$.optionText").value("Option A"))
                .andExpect(jsonPath("$.optionScore").value(10))
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.locked").value(false));
    }

    @Test
    void testGetOptionById() throws Exception {
        UUID id = UUID.randomUUID();
        OptionDto dto = new OptionDto(id, UUID.randomUUID(), "Option B", 5L, false, false);

        Mockito.when(optionService.getOptionById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/options/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionId").value(id.toString()))
                .andExpect(jsonPath("$.optionText").value("Option B"))
                .andExpect(jsonPath("$.optionScore").value(5))
                .andExpect(jsonPath("$.correct").value(false))
                .andExpect(jsonPath("$.locked").value(false));
    }

    @Test
    void testGetAllOptions() throws Exception {
        OptionDto o1 = new OptionDto(UUID.randomUUID(), UUID.randomUUID(), "Option 1", 3L, false, false);
        OptionDto o2 = new OptionDto(UUID.randomUUID(), UUID.randomUUID(), "Option 2", 7L, true, false);

        Mockito.when(optionService.getAllOptions()).thenReturn(List.of(o1, o2));

        mockMvc.perform(get("/api/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].optionText").value("Option 1"))
                .andExpect(jsonPath("$[1].optionText").value("Option 2"));
    }

    @Test
    void testUpdateOption() throws Exception {
        UUID id = UUID.randomUUID();
        OptionDto request = new OptionDto(null, UUID.randomUUID(), "Updated Option", 15L, true, true);
        OptionDto response = new OptionDto(id, request.getQuestionId(), "Updated Option", 15L, true, true);

        Mockito.when(optionService.updateOption(eq(id), any(OptionDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/options/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionId").value(id.toString()))
                .andExpect(jsonPath("$.optionText").value("Updated Option"))
                .andExpect(jsonPath("$.optionScore").value(15))
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.locked").value(true));
    }

    @Test
    void testDeleteOption() throws Exception {
        UUID id = UUID.randomUUID();
        OptionDto remaining = new OptionDto(UUID.randomUUID(), UUID.randomUUID(), "Option Restante", 8L, false, false);

        Mockito.when(optionService.getAllOptions()).thenReturn(List.of(remaining));

        mockMvc.perform(delete("/api/options/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].optionText").value("Option Restante"));
    }

    @Test
    void testLockOption() throws Exception {
        UUID id = UUID.randomUUID();
        OptionDto locked = new OptionDto(id, UUID.randomUUID(), "Locked Option", 5L, false, true);

        Mockito.when(optionService.lockOption(id)).thenReturn(locked);

        mockMvc.perform(patch("/api/options/{id}/lock", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(true));
    }

    @Test
    void testUnlockOption() throws Exception {
        UUID id = UUID.randomUUID();
        OptionDto unlocked = new OptionDto(id, UUID.randomUUID(), "Unlocked Option", 5L, false, false);

        Mockito.when(optionService.unlockOption(id)).thenReturn(unlocked);

        mockMvc.perform(patch("/api/options/{id}/unlock", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(false));
    }
    @Test
    void testGetOptionsByQuestionId() throws Exception {
        UUID questionId = UUID.randomUUID();
        OptionDto o1 = new OptionDto(UUID.randomUUID(), questionId, "Option X", 2L, false, false);
        OptionDto o2 = new OptionDto(UUID.randomUUID(), questionId, "Option Y", 4L, true, false);

        Mockito.when(optionService.getOptionByQuestionId(questionId)).thenReturn(List.of(o1, o2));

        mockMvc.perform(get("/api/options/byQuestion/{questionId}", questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].optionText").value("Option X"))
                .andExpect(jsonPath("$[1].optionText").value("Option Y"));
    }

}
