package horizon.surveyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import horizon.surveyservice.DTO.AssignedQuestionDto;
import horizon.surveyservice.service.AssignedQuestionService;
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

@WebMvcTest(controllers = AssignedQuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AssignedQuestionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssignedQuestionService assignedQuestionService;

    @Test
    void testAssignQuestionToSurvey() throws Exception {
        UUID surveyId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        AssignedQuestionDto assigned = new AssignedQuestionDto();
        assigned.setSurveyId(surveyId);
        assigned.setQuestionId(questionId);

        Mockito.when(assignedQuestionService.assignQuestionToSurvey(eq(surveyId), eq(questionId), any(), any()))
                .thenReturn(assigned);

        mockMvc.perform(post("/api/assigned-question/assign")
                        .param("surveyId", surveyId.toString())
                        .param("questionId", questionId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.surveyId").value(surveyId.toString()))
                .andExpect(jsonPath("$.questionId").value(questionId.toString()));
    }

    @Test
    void testUnassignQuestionFromSurvey() throws Exception {
        UUID surveyId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        mockMvc.perform(delete("/api/assigned-question/unassign")
                        .param("surveyId", surveyId.toString())
                        .param("questionId", questionId.toString()))
                .andExpect(status().isNoContent());

        Mockito.verify(assignedQuestionService).unassignQuestionFromSurvey(surveyId, questionId);
    }

    @Test
    void testGetAssignedQuestionsBySurvey() throws Exception {
        UUID surveyId = UUID.randomUUID();
        AssignedQuestionDto a1 = new AssignedQuestionDto();
        a1.setSurveyId(surveyId);
        a1.setQuestionId(UUID.randomUUID());

        AssignedQuestionDto a2 = new AssignedQuestionDto();
        a2.setSurveyId(surveyId);
        a2.setQuestionId(UUID.randomUUID());

        Mockito.when(assignedQuestionService.getAssignedQuestionsBySurvey(surveyId))
                .thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/assigned-question/survey/{surveyId}", surveyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].surveyId").value(surveyId.toString()))
                .andExpect(jsonPath("$[1].surveyId").value(surveyId.toString()));
    }
}
