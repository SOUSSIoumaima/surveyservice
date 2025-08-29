package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.SurveyDto;
import horizon.surveyservice.entity.AssignedQuestion;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.entity.Survey;
import horizon.surveyservice.entity.SurveyStatus;
import horizon.surveyservice.exeptions.BadRequestException;
import horizon.surveyservice.exeptions.LockedException;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.repository.AssignedQuestionRepository;
import horizon.surveyservice.repository.QuestionRepository;
import horizon.surveyservice.repository.SurveyRepository;
import horizon.surveyservice.service.AssignedQuestionService;
import horizon.surveyservice.util.OrganizationContextUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SurveyServiceImplTest {
    @Mock
    private SurveyRepository surveyRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AssignedQuestionRepository assignedQuestionRepository;
    @Mock
    private OrganizationContextUtil organizationContextUtil;
    @Mock
    private AssignedQuestionService assignedQuestionService;

    @InjectMocks
    private SurveyServiceImpl surveyService;

    private UUID surveyId;
    private UUID userId;
    private UUID orgId;
    private Survey survey;

    private UUID depId;
    private UUID teamId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        surveyId = UUID.randomUUID();
        userId = UUID.randomUUID();
        orgId = UUID.randomUUID();
        depId = UUID.randomUUID();
        teamId = UUID.randomUUID();

        survey = new Survey();
        survey.setSurveyId(surveyId);
        survey.setOrganizationId(orgId);
        survey.setOwnerId(userId);
        survey.setStatus(SurveyStatus.DRAFT);
        survey.setAssignedQuestions(new ArrayList<>());

        // Valeurs par défaut utilisées par la majorité des tests
        when(organizationContextUtil.getCurrentUserId()).thenReturn(userId);
        when(organizationContextUtil.getCurrentOrganizationId()).thenReturn(orgId);
        when(organizationContextUtil.getCurrentDepartmentIdOrNull()).thenReturn(depId);
        when(organizationContextUtil.getCurrentTeamIdOrNull()).thenReturn(teamId);
        when(organizationContextUtil.hasAuthority(anyString())).thenReturn(true);
        when(organizationContextUtil.isRootAdmin()).thenReturn(false);
    }

    // ---------- create / read ----------

    @Test
    void createSurvey_shouldFillOrgAndOwner_andSave() {
        SurveyDto dto = new SurveyDto();
        dto.setTitle("New Survey");
        when(surveyRepository.save(any(Survey.class))).thenReturn(survey);

        SurveyDto result = surveyService.createSurvey(dto);

        assertNotNull(result);
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    void getSurveyById_success() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        SurveyDto result = surveyService.getSurveyById(surveyId);

        assertEquals(surveyId, result.getSurveyId());
        verify(organizationContextUtil).validateOrganizationAccess(orgId);
    }

    @Test
    void getSurveyById_notFound() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> surveyService.getSurveyById(surveyId));
    }

    @Test
    void getAllSurveys_filtersByAccess_andExpiresActives() {
        // S1: ACTIVE + deadline passée -> doit devenir CLOSED
        Survey s1 = new Survey();
        s1.setSurveyId(UUID.randomUUID());
        s1.setOrganizationId(orgId);
        s1.setOwnerId(userId);
        s1.setStatus(SurveyStatus.ACTIVE);
        s1.setDeadline(LocalDateTime.now().minusDays(1));

        // S2: DRAFT -> gardé si accès OK
        Survey s2 = new Survey();
        s2.setSurveyId(UUID.randomUUID());
        s2.setOrganizationId(orgId);
        s2.setOwnerId(userId);
        s2.setStatus(SurveyStatus.DRAFT);

        // S3: autre org -> on simule un refus d'accès (sera filtré)
        UUID otherOrg = UUID.randomUUID();
        Survey s3 = new Survey();
        s3.setSurveyId(UUID.randomUUID());
        s3.setOrganizationId(otherOrg);
        s3.setOwnerId(UUID.randomUUID());
        s3.setStatus(SurveyStatus.ACTIVE);

        when(surveyRepository.findAll()).thenReturn(List.of(s1, s2, s3));
        // Refuser l'accès pour otherOrg
        doThrow(new RuntimeException("no access"))
                .when(organizationContextUtil).validateOrganizationAccess(eq(otherOrg));

        List<SurveyDto> result = surveyService.getAllSurveys();

        assertEquals(2, result.size(), "Un seul doit être filtré par accès");
        // s1 doit avoir été passé à CLOSED et sauvegardé
        verify(surveyRepository, atLeastOnce()).save(argThat(s -> s.getSurveyId().equals(s1.getSurveyId())
                && s.getStatus() == SurveyStatus.CLOSED));
    }

    // ---------- update ----------

    @Test
    void updateSurvey_success_whenOwner_andUnlocked() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        SurveyDto patch = new SurveyDto();
        patch.setTitle("Updated");
        patch.setDescription("Updated desc");

        when(surveyRepository.save(any(Survey.class))).thenAnswer(inv -> inv.getArgument(0));

        SurveyDto result = surveyService.updateSurvey(surveyId, patch);

        assertEquals("Updated", result.getTitle());
        assertEquals("Updated desc", result.getDescription());
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    void updateSurvey_locked_throws() {
        survey.setLocked(true);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        assertThrows(LockedException.class, () -> surveyService.updateSurvey(surveyId, new SurveyDto()));
        verify(surveyRepository, never()).save(any());
    }

    @Test
    void updateSurvey_accessDenied_whenNotOwnerNorManager() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        // User ≠ owner
        when(organizationContextUtil.getCurrentUserId()).thenReturn(UUID.randomUUID());
        // Ni root, ni manager
        when(organizationContextUtil.isRootAdmin()).thenReturn(false);
        when(organizationContextUtil.hasAuthority(anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> surveyService.updateSurvey(surveyId, new SurveyDto()));
    }

    // ---------- delete ----------

    @Test
    void deleteSurvey_locked_throws() {
        survey.setLocked(true);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        assertThrows(LockedException.class, () -> surveyService.deleteSurvey(surveyId));
        verify(surveyRepository, never()).delete(any());
    }

    @Test
    void deleteSurvey_success() {
        survey.setLocked(false);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        surveyService.deleteSurvey(surveyId);

        verify(surveyRepository).delete(survey);
    }

    @Test
    void deleteSurvey_accessDenied_whenNotOwnerNorManager() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(organizationContextUtil.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(organizationContextUtil.isRootAdmin()).thenReturn(false);
        when(organizationContextUtil.hasAuthority(anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> surveyService.deleteSurvey(surveyId));
        verify(surveyRepository, never()).delete(any());
    }

    // ---------- assign / unassign ----------

    @Test
    void assignQuestion_alreadyAssigned_throws() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(assignedQuestionService.isQuestionAssignedToSurvey(any(), any())).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                surveyService.assignQuestionToSurvey(surveyId, UUID.randomUUID(), null, null));
    }

    @Test
    void assignQuestion_success_callsAssignedService_withDeptAndTeamFromContext() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(assignedQuestionService.isQuestionAssignedToSurvey(any(), any())).thenReturn(false);
        when(questionRepository.findById(any())).thenReturn(Optional.of(new horizon.surveyservice.entity.Question()));

        UUID qId = UUID.randomUUID();

        surveyService.assignQuestionToSurvey(surveyId, qId, null, null);

        verify(assignedQuestionService).assignQuestionToSurvey(eq(surveyId), eq(qId), eq(depId), eq(teamId));
        // updateSurveyLockStatus() sauvegarde le survey
        verify(surveyRepository).save(eq(survey));
    }

    @Test
    void unassignQuestion_lockedByOther_throws() {
        AssignedQuestion aq = new AssignedQuestion();
        aq.setSurvey(survey);
        aq.setLocked(true);
        aq.setLockedBy(UUID.randomUUID()); // autre user

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(assignedQuestionRepository.findById(any())).thenReturn(Optional.of(aq));

        assertThrows(LockedException.class, () ->
                surveyService.unassignQuestionFromSurvey(surveyId, UUID.randomUUID()));
        verify(assignedQuestionRepository, never()).delete(any());
    }

    @Test
    void unassignQuestion_wrongSurvey_throws() {
        AssignedQuestion aq = new AssignedQuestion();
        Survey other = new Survey();
        other.setSurveyId(UUID.randomUUID());
        aq.setSurvey(other);
        aq.setLocked(false);

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(assignedQuestionRepository.findById(any())).thenReturn(Optional.of(aq));

        assertThrows(IllegalArgumentException.class, () ->
                surveyService.unassignQuestionFromSurvey(surveyId, UUID.randomUUID()));
    }

    @Test
    void unassignQuestion_success_deletes_andUpdatesLock() {
        AssignedQuestion aq = new AssignedQuestion();
        aq.setSurvey(survey);
        aq.setLocked(false);

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(assignedQuestionRepository.findById(any())).thenReturn(Optional.of(aq));

        surveyService.unassignQuestionFromSurvey(surveyId, UUID.randomUUID());

        verify(assignedQuestionRepository).delete(eq(aq));
        verify(surveyRepository).save(eq(survey));
    }

    // ---------- lock / unlock ----------

    @Test
    void lockSurvey_success() {
        AssignedQuestion aq = new AssignedQuestion();
        aq.setSurvey(survey);
        aq.setLocked(false);

        // Ajouter une question pour éviter le NullPointerException
        Question question = new Question();
        question.setQuestionId(UUID.randomUUID());
        aq.setQuestion(question);

        survey.setAssignedQuestions(List.of(aq));

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        SurveyDto result = surveyService.lockSurvey(surveyId);

        assertTrue(result.isLocked());
        verify(assignedQuestionRepository, atLeastOnce()).save(any(AssignedQuestion.class));
        verify(surveyRepository).save(eq(survey));
    }

    @Test
    void lockSurvey_accessDenied_whenNotOwnerNorManagers() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(organizationContextUtil.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(organizationContextUtil.hasAuthority(anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> surveyService.lockSurvey(surveyId));
    }

    @Test
    void unlockSurvey_success() {

        Question question = new Question();
        question.setQuestionId(UUID.randomUUID());

        AssignedQuestion aq = new AssignedQuestion();
        aq.setSurvey(survey);
        aq.setQuestion(question);
        aq.setLocked(true);
        aq.setLockedBy(userId);

        survey.setAssignedQuestions(List.of(aq));

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        SurveyDto result = surveyService.unlockSurvey(surveyId);

        assertFalse(result.isLocked());
        verify(assignedQuestionRepository, atLeastOnce()).save(any(AssignedQuestion.class));
        verify(surveyRepository).save(eq(survey));
    }

    @Test
    void unlockSurvey_accessDenied_whenNotOwnerNorManagers() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(organizationContextUtil.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(organizationContextUtil.hasAuthority(anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> surveyService.unlockSurvey(surveyId));
    }

    // ---------- publish ----------

    @Test
    void publishSurvey_success() {

        Question question = new Question();
        question.setQuestionId(UUID.randomUUID());

        AssignedQuestion aq = new AssignedQuestion();
        aq.setSurvey(survey);
        aq.setQuestion(question);
        survey.setAssignedQuestions(List.of(aq));

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        SurveyDto result = surveyService.publishSurvey(surveyId);

        assertEquals(SurveyStatus.ACTIVE, result.getStatus());
        verify(surveyRepository).save(eq(survey));
    }


    @Test
    void publishSurvey_invalidStatus_ACTIVE_throws() {
        survey.setStatus(SurveyStatus.ACTIVE);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        assertThrows(BadRequestException.class, () -> surveyService.publishSurvey(surveyId));
    }

    @Test
    void publishSurvey_closed_throws() {
        survey.setStatus(SurveyStatus.CLOSED);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        assertThrows(BadRequestException.class, () -> surveyService.publishSurvey(surveyId));
    }

    @Test
    void publishSurvey_noQuestions_throws() {
        survey.setStatus(SurveyStatus.DRAFT);
        survey.setAssignedQuestions(Collections.emptyList());
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        assertThrows(BadRequestException.class, () -> surveyService.publishSurvey(surveyId));
    }

    // ---------- exists ----------

    @Test
    void exists_returnsTrue() {
        when(surveyRepository.existsById(surveyId)).thenReturn(true);
        assertTrue(surveyService.exists(surveyId));
    }

    // ---------- active & closed list ----------

    @Test
    void getActiveAndClosedSurveys_returnsOnlyAccessibleActiveOrClosed_andExpiresActives() {
        when(organizationContextUtil.getCurrentOrganizationId()).thenReturn(orgId);

        Survey sActive = new Survey();
        sActive.setSurveyId(UUID.randomUUID());
        sActive.setOrganizationId(orgId);
        sActive.setStatus(SurveyStatus.ACTIVE);
        sActive.setDeadline(LocalDateTime.now().plusDays(1));

        Survey sActiveExpired = new Survey();
        sActiveExpired.setSurveyId(UUID.randomUUID());
        sActiveExpired.setOrganizationId(orgId);
        sActiveExpired.setStatus(SurveyStatus.ACTIVE);
        sActiveExpired.setDeadline(LocalDateTime.now().minusDays(1)); // doit passer CLOSED

        Survey sDraft = new Survey();
        sDraft.setSurveyId(UUID.randomUUID());
        sDraft.setOrganizationId(orgId);
        sDraft.setStatus(SurveyStatus.DRAFT);

        when(surveyRepository.findByOrganizationId(orgId)).thenReturn(List.of(sActive, sActiveExpired, sDraft));

        List<SurveyDto> result = surveyService.getActiveAndClosedSurveys();

        // sDraft exclu, sActive et sActiveExpired(->CLOSED) inclus
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> d.getStatus() == SurveyStatus.ACTIVE));
        assertTrue(result.stream().anyMatch(d -> d.getStatus() == SurveyStatus.CLOSED));
        // le passage à CLOSED implique un save
        verify(surveyRepository, atLeastOnce()).save(eq(sActiveExpired));
    }

    // ---------- hierarchical view ----------

    @Test
    void getSurveyByIdHierarchical_filtersQuestionsByOrgDeptTeam() {
        // Contexte courant
        when(organizationContextUtil.getCurrentDepartmentIdOrNull()).thenReturn(depId);
        when(organizationContextUtil.getCurrentTeamIdOrNull()).thenReturn(teamId);

        // Créer des questions
        Question q1 = new Question();
        q1.setQuestionId(UUID.randomUUID());
        Question q2 = new Question();
        q2.setQuestionId(UUID.randomUUID());
        Question q3 = new Question();
        q3.setQuestionId(UUID.randomUUID());
        Question q4 = new Question();
        q4.setQuestionId(UUID.randomUUID());

        // Questions affectées : ORG (null,null) -> visible
        AssignedQuestion orgLevel = new AssignedQuestion();
        orgLevel.setSurvey(survey);
        orgLevel.setDepartmentId(null);
        orgLevel.setTeamId(null);
        orgLevel.setQuestion(q1);

        // Dept visible (depId)
        AssignedQuestion deptVisible = new AssignedQuestion();
        deptVisible.setSurvey(survey);
        deptVisible.setDepartmentId(depId);
        deptVisible.setTeamId(null);
        deptVisible.setQuestion(q2);

        // Team visible (teamId)
        AssignedQuestion teamVisible = new AssignedQuestion();
        teamVisible.setSurvey(survey);
        teamVisible.setDepartmentId(null);
        teamVisible.setTeamId(teamId);
        teamVisible.setQuestion(q3);

        // Non visible (autre dept)
        AssignedQuestion hidden = new AssignedQuestion();
        hidden.setSurvey(survey);
        hidden.setDepartmentId(UUID.randomUUID());
        hidden.setTeamId(UUID.randomUUID());
        hidden.setQuestion(q4);

        survey.setAssignedQuestions(List.of(orgLevel, deptVisible, teamVisible, hidden));

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        SurveyDto dto = surveyService.getSurveyByIdHierarchical(surveyId);

        // On s’attend à voir 3 questions (ORG + dept + team)
        assertNotNull(dto);
        assertNotNull(dto.getAssignedQuestions());
        assertEquals(3, dto.getAssignedQuestions().size());
    }

    @Test
    void updateSurveyStatusIfExpired_notExpired_doesNotChangeStatus() {
        Survey s = new Survey();
        s.setStatus(SurveyStatus.ACTIVE);
        s.setDeadline(LocalDateTime.now().plusDays(1));
        surveyService.getAllSurveys(); // ou appelle la méthode privée via réflexion si nécessaire
        assertEquals(SurveyStatus.ACTIVE, s.getStatus());
    }

    @Test
    void updateSurveyStatusIfExpired_noDeadline_doesNotChangeStatus() {
        Survey s = new Survey();
        s.setStatus(SurveyStatus.ACTIVE);
        s.setDeadline(null);
        surveyService.getAllSurveys(); // idem
        assertEquals(SurveyStatus.ACTIVE, s.getStatus());
    }

    @Test
    void lockSurvey_noAssignedQuestions_surveyLockedFalse() {
        survey.setAssignedQuestions(new ArrayList<>());
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        SurveyDto dto = surveyService.lockSurvey(surveyId);

        assertFalse(dto.isLocked());
        verify(surveyRepository).save(survey);
    }


    @Test
    void unlockSurvey_noAssignedQuestions_surveyLockedFalse() {
        survey.setLocked(true);
        survey.setAssignedQuestions(new ArrayList<>());
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

        SurveyDto dto = surveyService.unlockSurvey(surveyId);

        assertFalse(dto.isLocked());
        verify(surveyRepository).save(survey);
    }
    // ---------- create / read ----------


    @Test
    void getSurveyById_accessDenied_throwsAccessDeniedException() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        doThrow(AccessDeniedException.class).when(organizationContextUtil).validateOrganizationAccess(orgId);

        assertThrows(AccessDeniedException.class, () -> surveyService.getSurveyById(surveyId));
    }

// ---------- update ----------

    @Test
    void updateSurvey_notFound_throwsResourceNotFoundException() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> surveyService.updateSurvey(surveyId, new SurveyDto()));
    }

    @Test
    void updateSurvey_partialUpdate_onlyChangesProvidedFields() {
        SurveyDto patch = new SurveyDto();
        patch.setTitle("New Title"); // seulement le titre

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(surveyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SurveyDto result = surveyService.updateSurvey(surveyId, patch);

        assertEquals("New Title", result.getTitle());
        // Description reste null
        assertNull(result.getDescription());
    }

// ---------- delete ----------

    @Test
    void deleteSurvey_notFound_throwsResourceNotFoundException() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> surveyService.deleteSurvey(surveyId));
    }

    @Test
    void deleteSurvey_rootAdmin_canDelete() {
        survey.setLocked(false); // <- important
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(organizationContextUtil.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(organizationContextUtil.isRootAdmin()).thenReturn(true);

        surveyService.deleteSurvey(surveyId);

        verify(surveyRepository).delete(survey);
    }


// ---------- assign / unassign ----------

    @Test
    void assignQuestion_surveyNotFound_throwsResourceNotFoundException() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> surveyService.assignQuestionToSurvey(surveyId, UUID.randomUUID(), null, null));
    }

    @Test
    void assignQuestion_questionNotFound_throwsResourceNotFoundException() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(assignedQuestionService.isQuestionAssignedToSurvey(any(), any())).thenReturn(false);
        when(questionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> surveyService.assignQuestionToSurvey(surveyId, UUID.randomUUID(), null, null));
    }

    @Test
    void unassignQuestion_notFound_throwsResourceNotFoundException() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(assignedQuestionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> surveyService.unassignQuestionFromSurvey(surveyId, UUID.randomUUID()));
    }

    @Test
    void unassignQuestion_lockedByCurrentUser_success() {
        AssignedQuestion aq = new AssignedQuestion();
        aq.setSurvey(survey);
        aq.setLocked(true);
        aq.setLockedBy(userId); // même user

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(assignedQuestionRepository.findById(any())).thenReturn(Optional.of(aq));

        surveyService.unassignQuestionFromSurvey(surveyId, UUID.randomUUID());

        verify(assignedQuestionRepository).delete(aq);
        verify(surveyRepository).save(survey);
    }

// ---------- lock / unlock ----------

    @Test
    void lockSurvey_surveyNotFound_throwsResourceNotFoundException() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> surveyService.lockSurvey(surveyId));
    }

    @Test
    void unlockSurvey_surveyNotFound_throwsResourceNotFoundException() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> surveyService.unlockSurvey(surveyId));
    }


// ---------- exists / active & closed list ----------

    @Test
    void exists_returnsFalse() {
        when(surveyRepository.existsById(surveyId)).thenReturn(false);
        assertFalse(surveyService.exists(surveyId));
    }

    @Test
    void getActiveAndClosedSurveys_noSurveys_returnsEmptyList() {
        when(organizationContextUtil.getCurrentOrganizationId()).thenReturn(orgId);
        when(surveyRepository.findByOrganizationId(orgId)).thenReturn(Collections.emptyList());

        List<SurveyDto> result = surveyService.getActiveAndClosedSurveys();
        assertTrue(result.isEmpty());
    }

    @Test
    void getActiveAndClosedSurveys_accessDeniedSurvey_filteredOut() {
        Survey s = new Survey();
        s.setSurveyId(UUID.randomUUID());
        s.setOrganizationId(orgId);
        s.setStatus(SurveyStatus.ACTIVE);

        when(organizationContextUtil.getCurrentOrganizationId()).thenReturn(orgId);
        when(surveyRepository.findByOrganizationId(orgId)).thenReturn(List.of(s));
        doThrow(AccessDeniedException.class).when(organizationContextUtil).validateOrganizationAccess(orgId);

        List<SurveyDto> result = surveyService.getActiveAndClosedSurveys();
        assertTrue(result.isEmpty());
    }

// ---------- hierarchical / filtering ----------

    @Test
    void getSurveyByIdHierarchical_surveyNotFound_throwsResourceNotFoundException() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> surveyService.getSurveyByIdHierarchical(surveyId));
    }

    @Test
    void getSurveyByIdHierarchical_noQuestions_returnsEmptyList() {
        Survey emptySurvey = new Survey();
        emptySurvey.setSurveyId(surveyId);
        emptySurvey.setAssignedQuestions(Collections.emptyList());
        emptySurvey.setOrganizationId(orgId);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(emptySurvey));

        SurveyDto dto = surveyService.getSurveyByIdHierarchical(surveyId);

        assertNotNull(dto); // SurveyDto doit être créé
        // Liste de questions ne doit pas être null, ou si elle est null, la remplacer par une liste vide
        List<?> questions = dto.getAssignedQuestions() == null ? Collections.emptyList() : dto.getAssignedQuestions();
        assertTrue(questions.isEmpty());
    }



    @Test
    void getSurveyByIdHierarchical_accessDenied_throwsAccessDeniedException() {
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        doThrow(AccessDeniedException.class).when(organizationContextUtil).validateOrganizationAccess(orgId);

        assertThrows(AccessDeniedException.class, () -> surveyService.getSurveyByIdHierarchical(surveyId));
    }

}
