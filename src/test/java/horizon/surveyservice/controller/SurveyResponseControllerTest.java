package horizon.surveyservice.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import horizon.surveyservice.DTO.SurveyResponseDto;
import horizon.surveyservice.service.SurveyResponseService;
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

@WebMvcTest(SurveyResponseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SurveyResponseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SurveyResponseService surveyResponseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSubmitSurveyResponse() throws Exception {
        SurveyResponseDto request = new SurveyResponseDto();
        request.setSurveyId(UUID.randomUUID());
        request.setFinal(false);

        SurveyResponseDto response = new SurveyResponseDto();
        response.setSurveyResponseId(UUID.randomUUID());
        response.setFinal(false);

        Mockito.when(surveyResponseService.submitSurveyResponse(any(SurveyResponseDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/survey-response")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surveyResponseId").value(response.getSurveyResponseId().toString()))
                .andExpect(jsonPath("$.final").value(false));
    }

    @Test
    void testGetAllSurveyResponses() throws Exception {
        SurveyResponseDto r1 = new SurveyResponseDto();
        r1.setSurveyResponseId(UUID.randomUUID());
        SurveyResponseDto r2 = new SurveyResponseDto();
        r2.setSurveyResponseId(UUID.randomUUID());

        Mockito.when(surveyResponseService.getAllSurveyResponses()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/survey-response"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].surveyResponseId").value(r1.getSurveyResponseId().toString()))
                .andExpect(jsonPath("$[1].surveyResponseId").value(r2.getSurveyResponseId().toString()));
    }

    @Test
    void testGetSurveyResponseById() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyResponseDto response = new SurveyResponseDto();
        response.setSurveyResponseId(id);

        Mockito.when(surveyResponseService.getSurveyResponseById(id)).thenReturn(response);

        mockMvc.perform(get("/api/survey-response/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surveyResponseId").value(id.toString()));
    }

    @Test
    void testUpdateSurveyResponse() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyResponseDto request = new SurveyResponseDto();
        request.setFinal(false);

        SurveyResponseDto response = new SurveyResponseDto();
        response.setSurveyResponseId(id);
        response.setFinal(false);

        Mockito.when(surveyResponseService.updateSurveyResponse(eq(id), any(SurveyResponseDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/survey-response/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surveyResponseId").value(id.toString()))
                .andExpect(jsonPath("$.final").value(false));
    }

    @Test
    void testFinalizeSurveyResponse() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyResponseDto existing = new SurveyResponseDto();
        existing.setSurveyResponseId(id);
        existing.setFinal(false);

        SurveyResponseDto finalized = new SurveyResponseDto();
        finalized.setSurveyResponseId(id);
        finalized.setFinal(true);

        Mockito.when(surveyResponseService.getSurveyResponseById(id)).thenReturn(existing);
        Mockito.when(surveyResponseService.updateSurveyResponse(eq(id), any(SurveyResponseDto.class)))
                .thenReturn(finalized);

        mockMvc.perform(put("/api/survey-response/{id}/final", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.final").value(true));
    }

    @Test
    void testDeleteSurveyResponse() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyResponseDto remaining = new SurveyResponseDto();
        remaining.setSurveyResponseId(UUID.randomUUID());

        Mockito.when(surveyResponseService.getAllSurveyResponses()).thenReturn(List.of(remaining));

        mockMvc.perform(delete("/api/survey-response/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].surveyResponseId").value(remaining.getSurveyResponseId().toString()));
    }
    @Test
    void testFinalizeSurveyResponseAlreadyFinalized() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyResponseDto alreadyFinal = new SurveyResponseDto();
        alreadyFinal.setSurveyResponseId(id);
        alreadyFinal.setFinal(true);

        Mockito.when(surveyResponseService.getSurveyResponseById(id)).thenReturn(alreadyFinal);

        mockMvc.perform(put("/api/survey-response/{id}/final", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.final").value(true));
    }

}
