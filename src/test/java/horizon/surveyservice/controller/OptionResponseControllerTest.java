package horizon.surveyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import horizon.surveyservice.DTO.OptionResponseDto;
import horizon.surveyservice.service.OptionResponseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(OptionResponseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OptionResponseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OptionResponseService optionResponseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSubmitOptionResponse() throws Exception {
        OptionResponseDto request = new OptionResponseDto();
        request.setOptionId(UUID.randomUUID());
        request.setQuestionResponseId(UUID.randomUUID());
        request.setOptionText("Option A");
        request.setCorrect(true);
        request.setSelected(true);
        request.setOptionScore(10L);

        OptionResponseDto response = new OptionResponseDto();
        response.setOptionResponseId(UUID.randomUUID());
        response.setOptionId(request.getOptionId());
        response.setQuestionResponseId(request.getQuestionResponseId());
        response.setOptionText(request.getOptionText());
        response.setCorrect(request.isCorrect());
        response.setSelected(request.isSelected());
        response.setOptionScore(request.getOptionScore());

        Mockito.when(optionResponseService.submitOptionResponse(any(OptionResponseDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/option-responses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionResponseId").value(response.getOptionResponseId().toString()))
                .andExpect(jsonPath("$.optionText").value("Option A"))
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.selected").value(true));
    }

    @Test
    void testGetOptionResponseById() throws Exception {
        UUID id = UUID.randomUUID();
        OptionResponseDto response = new OptionResponseDto();
        response.setOptionResponseId(id);
        response.setOptionText("Option A");

        Mockito.when(optionResponseService.getOptionResponseById(id)).thenReturn(response);

        mockMvc.perform(get("/api/option-responses/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionResponseId").value(id.toString()))
                .andExpect(jsonPath("$.optionText").value("Option A"));
    }

    @Test
    void testGetOptionResponseByQuestionResponseId() throws Exception {
        UUID questionResponseId = UUID.randomUUID();
        OptionResponseDto response = new OptionResponseDto();
        response.setOptionResponseId(UUID.randomUUID());
        response.setOptionText("Option A");

        Mockito.when(optionResponseService.getOptionResponseByQuestionResponseId(questionResponseId))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/option-responses/byQuestion/{questionResponseId}", questionResponseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].optionText").value("Option A"));
    }

    @Test
    void testGetAllOptionResponses() throws Exception {
        OptionResponseDto response1 = new OptionResponseDto();
        response1.setOptionResponseId(UUID.randomUUID());
        response1.setOptionText("Option 1");

        OptionResponseDto response2 = new OptionResponseDto();
        response2.setOptionResponseId(UUID.randomUUID());
        response2.setOptionText("Option 2");

        Mockito.when(optionResponseService.getAllOptionResponses()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/option-responses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].optionText").value("Option 1"))
                .andExpect(jsonPath("$[1].optionText").value("Option 2"));
    }

    @Test
    void testUpdateOptionResponse() throws Exception {
        UUID id = UUID.randomUUID();
        OptionResponseDto request = new OptionResponseDto();
        request.setOptionText("Updated Option");
        request.setCorrect(true);

        OptionResponseDto response = new OptionResponseDto();
        response.setOptionResponseId(id);
        response.setOptionText(request.getOptionText());
        response.setCorrect(request.isCorrect());

        Mockito.when(optionResponseService.updateOptionResponse(eq(id), any(OptionResponseDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/option-responses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionResponseId").value(id.toString()))
                .andExpect(jsonPath("$.optionText").value("Updated Option"))
                .andExpect(jsonPath("$.correct").value(true));
    }

    @Test
    void testDeleteOptionResponse() throws Exception {
        UUID id = UUID.randomUUID();
        OptionResponseDto remaining = new OptionResponseDto();
        remaining.setOptionResponseId(UUID.randomUUID());
        remaining.setOptionText("Remaining Option");

        Mockito.when(optionResponseService.getAllOptionResponses()).thenReturn(List.of(remaining));

        mockMvc.perform(delete("/api/option-responses/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].optionText").value("Remaining Option"));
    }
}
