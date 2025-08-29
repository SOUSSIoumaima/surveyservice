package horizon.surveyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import horizon.surveyservice.DTO.SurveyDto;
import horizon.surveyservice.entity.SurveyResponseType;
import horizon.surveyservice.entity.SurveyStatus;
import horizon.surveyservice.entity.SurveyType;
import horizon.surveyservice.service.SurveyService;
import horizon.surveyservice.util.OrganizationContextUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = SurveyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SurveyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SurveyService surveyService;

    @MockBean
    private OrganizationContextUtil orgContextUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateSurvey() throws Exception {
        SurveyDto request = new SurveyDto();
        request.setTitle("Customer Feedback");
        request.setDescription("Feedback survey");
        request.setType(SurveyType.FEEDBACK);
        request.setResponseType(SurveyResponseType.ALL_IN_ONE_PAGE);
        request.setLocked(false);

        SurveyDto response = new SurveyDto();
        response.setSurveyId(UUID.randomUUID());
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setType(request.getType());
        response.setResponseType(request.getResponseType());
        response.setLocked(request.isLocked());
        response.setStatus(SurveyStatus.DRAFT);
        response.setCreatedAt(LocalDateTime.now());

        Mockito.when(surveyService.createSurvey(any(SurveyDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/surveys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.surveyId").value(response.getSurveyId().toString()))
                .andExpect(jsonPath("$.title").value("Customer Feedback"))
                .andExpect(jsonPath("$.description").value("Feedback survey"))
                .andExpect(jsonPath("$.locked").value(false));
    }

    @Test
    void testGetSurveyById() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyDto dto = new SurveyDto();
        dto.setSurveyId(id);
        dto.setTitle("Survey 1");
        dto.setDescription("Test Survey");
        dto.setType(SurveyType.FEEDBACK);

        Mockito.when(surveyService.getSurveyById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/surveys/{surveyId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surveyId").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Survey 1"))
                .andExpect(jsonPath("$.description").value("Test Survey"));
    }

    @Test
    void testGetAllSurveys() throws Exception {
        SurveyDto s1 = new SurveyDto();
        s1.setSurveyId(UUID.randomUUID());
        s1.setTitle("Survey 1");

        SurveyDto s2 = new SurveyDto();
        s2.setSurveyId(UUID.randomUUID());
        s2.setTitle("Survey 2");

        Mockito.when(orgContextUtil.isRootAdmin()).thenReturn(true);
        Mockito.when(surveyService.getAllSurveys()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/surveys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Survey 1"))
                .andExpect(jsonPath("$[1].title").value("Survey 2"));
    }

    @Test
    void testUpdateSurvey() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyDto request = new SurveyDto();
        request.setTitle("Updated Survey");
        request.setDescription("Updated description");

        SurveyDto response = new SurveyDto();
        response.setSurveyId(id);
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());

        Mockito.when(surveyService.updateSurvey(eq(id), any(SurveyDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/surveys/{surveyId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surveyId").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Updated Survey"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void testDeleteSurvey() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyDto remaining = new SurveyDto();
        remaining.setSurveyId(UUID.randomUUID());
        remaining.setTitle("Remaining Survey");

        Mockito.when(surveyService.getAllSurveys()).thenReturn(List.of(remaining));

        mockMvc.perform(delete("/api/surveys/{surveyId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Remaining Survey"));
    }

    @Test
    void testLockUnlockSurvey() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyDto locked = new SurveyDto();
        locked.setSurveyId(id);
        locked.setLocked(true);

        SurveyDto unlocked = new SurveyDto();
        unlocked.setSurveyId(id);
        unlocked.setLocked(false);

        Mockito.when(surveyService.lockSurvey(id)).thenReturn(locked);
        Mockito.when(surveyService.unlockSurvey(id)).thenReturn(unlocked);

        mockMvc.perform(patch("/api/surveys/{id}/lock", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(true));

        mockMvc.perform(patch("/api/surveys/{id}/unlock", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(false));
    }

    @Test
    void testExists() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(surveyService.exists(id)).thenReturn(true);

        mockMvc.perform(get("/api/surveys/{id}/exists", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testPublishSurvey() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyDto published = new SurveyDto();
        published.setSurveyId(id);
        published.setStatus(SurveyStatus.DRAFT);

        Mockito.when(surveyService.publishSurvey(id)).thenReturn(published);

        mockMvc.perform(put("/api/surveys/{surveyId}/publish", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }
    @Test
    void testGetAllSurveys_nonRootAdmin() throws Exception {
        UUID orgId = UUID.randomUUID();
        SurveyDto s = new SurveyDto();
        s.setSurveyId(UUID.randomUUID());
        s.setTitle("Org Survey");

        Mockito.when(orgContextUtil.isRootAdmin()).thenReturn(false);
        Mockito.when(orgContextUtil.getCurrentOrganizationId()).thenReturn(orgId);
        Mockito.when(surveyService.getSurveysByOrganization(orgId)).thenReturn(List.of(s));

        mockMvc.perform(get("/api/surveys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Org Survey"));
    }

    @Test
    void testAssignQuestionToSurvey() throws Exception {
        UUID surveyId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        SurveyDto dto = new SurveyDto();
        dto.setSurveyId(surveyId);
        dto.setTitle("Survey with Question");

        Mockito.doNothing().when(surveyService).assignQuestionToSurvey(eq(surveyId), eq(questionId), any(), any());
        Mockito.when(surveyService.getSurveyById(surveyId)).thenReturn(dto);

        mockMvc.perform(post("/api/surveys/{surveyId}/question/{questionId}", surveyId, questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Survey with Question"));
    }

    @Test
    void testUnassignQuestionFromSurvey() throws Exception {
        UUID surveyId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        SurveyDto dto = new SurveyDto();
        dto.setSurveyId(surveyId);
        dto.setTitle("Survey without Question");

        Mockito.doNothing().when(surveyService).unassignQuestionFromSurvey(surveyId, questionId);
        Mockito.when(surveyService.getSurveyById(surveyId)).thenReturn(dto);

        mockMvc.perform(delete("/api/surveys/{surveyId}/question/{questionId}", surveyId, questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Survey without Question"));
    }

    @Test
    void testGetActiveAndClosedSurveys() throws Exception {
        SurveyDto s1 = new SurveyDto();
        s1.setSurveyId(UUID.randomUUID());
        s1.setTitle("Closed Survey");

        Mockito.when(surveyService.getActiveAndClosedSurveys()).thenReturn(List.of(s1));

        mockMvc.perform(get("/api/surveys/active-closed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Closed Survey"));
    }

    @Test
    void testGetSurveyByIdHierarchical() throws Exception {
        UUID id = UUID.randomUUID();
        SurveyDto dto = new SurveyDto();
        dto.setSurveyId(id);
        dto.setTitle("Hierarchical Survey");

        Mockito.when(surveyService.getSurveyByIdHierarchical(id)).thenReturn(dto);

        mockMvc.perform(get("/api/surveys/{surveyId}/hierarchical", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Hierarchical Survey"));
    }
}
