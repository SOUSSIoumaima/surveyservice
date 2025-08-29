package horizon.surveyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import horizon.surveyservice.DTO.QuestionDto;
import horizon.surveyservice.entity.QuestionType;
import horizon.surveyservice.service.QuestionService;
import horizon.surveyservice.util.OrganizationContextUtil;
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

@WebMvcTest(controllers = QuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class QuestionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private OrganizationContextUtil orgContextUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateQuestion() throws Exception {
        QuestionDto request = new QuestionDto();
        request.setSubject("Math");
        request.setQuestionText("What is 2+2?");
        request.setQuestionType(QuestionType.FREE_TEXT);
        request.setLocked(false);

        QuestionDto response = new QuestionDto();
        response.setQuestionId(UUID.randomUUID());
        response.setSubject(request.getSubject());
        response.setQuestionText(request.getQuestionText());
        response.setQuestionType(request.getQuestionType());
        response.setLocked(request.isLocked());

        Mockito.when(questionService.createQuestion(any(QuestionDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionId").value(response.getQuestionId().toString()))
                .andExpect(jsonPath("$.subject").value("Math"))
                .andExpect(jsonPath("$.questionText").value("What is 2+2?"))
                .andExpect(jsonPath("$.locked").value(false));
    }

    @Test
    void testGetQuestionById() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionDto dto = new QuestionDto();
        dto.setQuestionId(id);
        dto.setSubject("Science");
        dto.setQuestionText("What is H2O?");
        dto.setQuestionType(QuestionType.FREE_TEXT);
        dto.setLocked(false);

        Mockito.when(questionService.getQuestionById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/questions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(id.toString()))
                .andExpect(jsonPath("$.subject").value("Science"))
                .andExpect(jsonPath("$.questionText").value("What is H2O?"));
    }

    @Test
    void testGetAllQuestions() throws Exception {
        QuestionDto q1 = new QuestionDto();
        q1.setQuestionId(UUID.randomUUID());
        q1.setSubject("Math");
        q1.setQuestionText("Q1?");
        q1.setQuestionType(QuestionType.SINGLE_CHOICE_TEXT);

        QuestionDto q2 = new QuestionDto();
        q2.setQuestionId(UUID.randomUUID());
        q2.setSubject("Science");
        q2.setQuestionText("Q2?");
        q2.setQuestionType(QuestionType.MULTIPLE_CHOICE_TEXT);

        Mockito.when(orgContextUtil.isRootAdmin()).thenReturn(true);
        Mockito.when(questionService.getAllQuestions()).thenReturn(List.of(q1, q2));

        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("Math"))
                .andExpect(jsonPath("$[1].subject").value("Science"));
    }

    @Test
    void testUpdateQuestion() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionDto request = new QuestionDto();
        request.setSubject("Updated Subject");
        request.setQuestionText("Updated Text");
        request.setQuestionType(QuestionType.FREE_TEXT);
        request.setLocked(true);

        QuestionDto response = new QuestionDto();
        response.setQuestionId(id);
        response.setSubject(request.getSubject());
        response.setQuestionText(request.getQuestionText());
        response.setQuestionType(request.getQuestionType());
        response.setLocked(request.isLocked());

        Mockito.when(questionService.updateQuestion(eq(id), any(QuestionDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/questions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(id.toString()))
                .andExpect(jsonPath("$.subject").value("Updated Subject"))
                .andExpect(jsonPath("$.questionText").value("Updated Text"))
                .andExpect(jsonPath("$.locked").value(true));
    }

    @Test
    void testDeleteQuestion() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionDto remaining = new QuestionDto();
        remaining.setQuestionId(UUID.randomUUID());
        remaining.setSubject("Remaining");
        remaining.setQuestionText("Still there?");
        remaining.setQuestionType(QuestionType.FREE_TEXT);

        Mockito.when(questionService.getAllQuestions()).thenReturn(List.of(remaining));

        mockMvc.perform(delete("/api/questions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("Remaining"));
    }

    @Test
    void testLockUnlockQuestion() throws Exception {
        UUID id = UUID.randomUUID();
        QuestionDto locked = new QuestionDto();
        locked.setQuestionId(id);
        locked.setLocked(true);

        QuestionDto unlocked = new QuestionDto();
        unlocked.setQuestionId(id);
        unlocked.setLocked(false);

        Mockito.when(questionService.lockQuestion(id)).thenReturn(locked);
        Mockito.when(questionService.unlockQuestion(id)).thenReturn(unlocked);

        mockMvc.perform(patch("/api/questions/{id}/lock", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(true));

        mockMvc.perform(patch("/api/questions/{id}/unlock", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(false));
    }
    @Test
    void testGetAllQuestions_nonRootAdmin() throws Exception {
        QuestionDto q = new QuestionDto();
        q.setQuestionId(UUID.randomUUID());
        q.setSubject("History");
        q.setQuestionText("When was WW2?");
        q.setQuestionType(QuestionType.FREE_TEXT);

        UUID orgId = UUID.randomUUID();
        Mockito.when(orgContextUtil.isRootAdmin()).thenReturn(false);
        Mockito.when(orgContextUtil.getCurrentOrganizationId()).thenReturn(orgId);
        Mockito.when(questionService.getQuestionByOrganization(orgId)).thenReturn(List.of(q));

        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("History"));
    }

    @Test
    void testGetBySubject_rootAdmin() throws Exception {
        QuestionDto q = new QuestionDto();
        q.setQuestionId(UUID.randomUUID());
        q.setSubject("Physics");
        q.setQuestionText("Law of gravity?");
        q.setQuestionType(QuestionType.FREE_TEXT);

        Mockito.when(orgContextUtil.isRootAdmin()).thenReturn(true);
        Mockito.when(questionService.getBySubject("Physics")).thenReturn(List.of(q));

        mockMvc.perform(get("/api/questions/subject/{subject}", "Physics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("Physics"));
    }

    @Test
    void testGetBySubject_nonRootAdmin() throws Exception {
        QuestionDto q = new QuestionDto();
        q.setQuestionId(UUID.randomUUID());
        q.setSubject("Chemistry");
        q.setQuestionText("What is NaCl?");
        q.setQuestionType(QuestionType.FREE_TEXT);

        UUID orgId = UUID.randomUUID();
        Mockito.when(orgContextUtil.isRootAdmin()).thenReturn(false);
        Mockito.when(orgContextUtil.getCurrentOrganizationId()).thenReturn(orgId);
        Mockito.when(questionService.getBySubjectAndOrganization("Chemistry", orgId)).thenReturn(List.of(q));

        mockMvc.perform(get("/api/questions/subject/{subject}", "Chemistry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("Chemistry"));
    }

}
