package horizon.surveyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import horizon.surveyservice.DTO.OptionResponseDto;
import horizon.surveyservice.DTO.QuestionResponseDto;
import horizon.surveyservice.service.QuestionResponseService;
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


@WebMvcTest(QuestionResponseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class QuestionResponseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionResponseService questionResponseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSubmitQuestionResponse() throws Exception {
        QuestionResponseDto request = new QuestionResponseDto();
        request.setQuestionId(UUID.randomUUID());
        OptionResponseDto option = new OptionResponseDto();
        option.setOptionScore(5L);
        option.setSelected(true);
        request.setOptionResponses(List.of(option));

        QuestionResponseDto response = new QuestionResponseDto();
        response.setQuestionResponseId(UUID.randomUUID());
        response.setQuestionScore(5L);
        response.setOptionResponses(List.of(option));

        Mockito.when(questionResponseService.submitQuestionResponse(any(QuestionResponseDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/question-response")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionScore").value(5));
    }

    @Test
    void testGetAllQuestionResponses() throws Exception {
        QuestionResponseDto q1 = new QuestionResponseDto();
        q1.setQuestionResponseId(UUID.randomUUID());
        QuestionResponseDto q2 = new QuestionResponseDto();
        q2.setQuestionResponseId(UUID.randomUUID());

        Mockito.when(questionResponseService.getAllQuestionResponses()).thenReturn(List.of(q1, q2));

        mockMvc.perform(get("/api/question-response"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionResponseId").value(q1.getQuestionResponseId().toString()))
                .andExpect(jsonPath("$[1].questionResponseId").value(q2.getQuestionResponseId().toString()));
    }

    @Test
    void testGetQuestionResponseById() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionResponseDto response = new QuestionResponseDto();
        response.setQuestionResponseId(id);

        Mockito.when(questionResponseService.getQuestionResponseById(id)).thenReturn(response);

        mockMvc.perform(get("/api/question-response/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionResponseId").value(id.toString()));
    }

    @Test
    void testGetQuestionResponseBySurveyResponseId() throws Exception {
        UUID surveyResponseId = UUID.randomUUID();
        QuestionResponseDto response = new QuestionResponseDto();
        response.setQuestionResponseId(UUID.randomUUID());

        Mockito.when(questionResponseService.getQuestionResponseBySurveyResponseId(surveyResponseId))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/question-response/bySurvey/{surveyResponseId}", surveyResponseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionResponseId").value(response.getQuestionResponseId().toString()));
    }

    @Test
    void testUpdateQuestionResponse() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionResponseDto request = new QuestionResponseDto();
        OptionResponseDto option = new OptionResponseDto();
        option.setOptionScore(3L);
        option.setSelected(true);
        request.setOptionResponses(List.of(option));

        QuestionResponseDto response = new QuestionResponseDto();
        response.setQuestionResponseId(id);
        response.setQuestionScore(3L);
        response.setOptionResponses(List.of(option));

        Mockito.when(questionResponseService.updateQuestionResponse(eq(id), any(QuestionResponseDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/question-response/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionScore").value(3))
                .andExpect(jsonPath("$.questionResponseId").value(id.toString()));
    }

    @Test
    void testDeleteQuestionResponse() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionResponseDto remaining = new QuestionResponseDto();
        remaining.setQuestionResponseId(UUID.randomUUID());

        Mockito.when(questionResponseService.getAllQuestionResponses()).thenReturn(List.of(remaining));

        mockMvc.perform(delete("/api/question-response/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionResponseId").value(remaining.getQuestionResponseId().toString()));
    }
}
