package horizon.surveyservice.service.serviceimpl;

import horizon.surveyservice.DTO.SurveyDto;
import horizon.surveyservice.entity.AssignedQuestion;
import horizon.surveyservice.entity.Question;
import horizon.surveyservice.entity.Survey;
import horizon.surveyservice.entity.SurveyStatus;
import horizon.surveyservice.exeptions.BadRequestException;
import horizon.surveyservice.exeptions.ResourceNotFoundException;
import horizon.surveyservice.exeptions.LockedException;
import horizon.surveyservice.mapper.SurveyMapper;
import horizon.surveyservice.repository.AssignedQuestionRepository;
import horizon.surveyservice.repository.QuestionRepository;
import horizon.surveyservice.repository.SurveyRepository;
import horizon.surveyservice.service.AssignedQuestionService;
import horizon.surveyservice.service.SurveyService;
import horizon.surveyservice.util.OrganizationContextUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final AssignedQuestionRepository assignedQuestionRepository;
    private final OrganizationContextUtil organizationContextUtil;
    private final AssignedQuestionService assignedQuestionService;

    public SurveyServiceImpl(SurveyRepository surveyRepository, QuestionRepository questionRepository, OrganizationContextUtil organizationContextUtil,AssignedQuestionService assignedQuestionService, AssignedQuestionRepository assignedQuestionRepository)
    {
        this.organizationContextUtil = organizationContextUtil;
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.assignedQuestionService = assignedQuestionService;
        this.assignedQuestionRepository = assignedQuestionRepository;
    }

    private void updateSurveyStatusIfExpired(Survey survey) {
        if (survey.getStatus() == SurveyStatus.ACTIVE
                && survey.getDeadline() != null
                && LocalDateTime.now().isAfter(survey.getDeadline())) {

            survey.setStatus(SurveyStatus.CLOSED);
            surveyRepository.save(survey);
        }
    }
    private void updateSurveyLockStatus(Survey survey) {
        boolean allQuestionsLocked = survey.getAssignedQuestions()
                .stream()
                .allMatch(aq -> aq.getLocked() != null && aq.getLocked());
        survey.setLocked(allQuestionsLocked);
        surveyRepository.save(survey);
    }

    @Override
    public SurveyDto createSurvey(SurveyDto surveyDto) {
        UUID currentOrgId = organizationContextUtil.getCurrentOrganizationId();
        surveyDto.setOrganizationId(currentOrgId);
        UUID currentUserId = organizationContextUtil.getCurrentUserId();
        surveyDto.setOwnerId(currentUserId);
        Survey survey = SurveyMapper.toSurveyEntity(surveyDto);
        Survey saved=surveyRepository.save(survey);
        return SurveyMapper.toSurveyDto(saved);
    }

    @Override
    public List<SurveyDto> getAllSurveys() {
        List<Survey> surveys = surveyRepository.findAll();
        for (Survey survey : surveys) {
            updateSurveyStatusIfExpired(survey);
        }
        return surveys.stream()
                .filter(s -> {
                    try {
                        organizationContextUtil.validateOrganizationAccess(s.getOrganizationId());
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(SurveyMapper::toSurveyDto)
                .collect(Collectors.toList());

    }

    @Override
    public SurveyDto getSurveyById(UUID surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(()-> new ResourceNotFoundException("Survey not found with id " + surveyId));
        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());
        updateSurveyStatusIfExpired(survey);
        return SurveyMapper.toSurveyDto(survey);
    }

    @Override
    public SurveyDto updateSurvey(UUID surveyId, SurveyDto surveyDto) {
        Survey existing = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id " + surveyId));

        organizationContextUtil.validateOrganizationAccess(existing.getOrganizationId());

        UUID currentUserId = organizationContextUtil.getCurrentUserId();

        boolean isOwner = existing.getOwnerId().equals(currentUserId);
        boolean isRootAdmin = organizationContextUtil.isRootAdmin();
        boolean isOrgManager = organizationContextUtil.hasAuthority("ORG_MANAGER");

        if (!(isOwner || isRootAdmin || isOrgManager)) {
            throw new AccessDeniedException("Only the owner, ORG_MANAGER, or SYS_ADMIN_ROOT can update this survey.");
        }

        if (existing.isLocked()) {
            throw new LockedException("Survey is locked and cannot be updated");
        }

        existing.setTitle(surveyDto.getTitle());
        existing.setDescription(surveyDto.getDescription());
        existing.setStatus(surveyDto.getStatus());
        existing.setDeadline(surveyDto.getDeadline());
        existing.setType(surveyDto.getType());
        existing.setResponseType(surveyDto.getResponseType());

        Survey updated = surveyRepository.save(existing);
        return SurveyMapper.toSurveyDto(updated);
    }

    @Override
    public void deleteSurvey(UUID surveyId) {
        Survey existing = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id " + surveyId));

        organizationContextUtil.validateOrganizationAccess(existing.getOrganizationId());

        UUID currentUserId = organizationContextUtil.getCurrentUserId();

        boolean isOwner = existing.getOwnerId().equals(currentUserId);
        boolean isRootAdmin = organizationContextUtil.isRootAdmin();
        boolean isOrgManager = organizationContextUtil.hasAuthority("ORG_MANAGER");

        if (!(isOwner || isRootAdmin || isOrgManager)) {
            throw new AccessDeniedException("Only the owner, ORG_MANAGER, or SYS_ADMIN_ROOT can delete this survey.");
        }

        if (existing.isLocked()) {
            throw new LockedException("Survey is locked and cannot be deleted");
        }

        surveyRepository.delete(existing);
    }

    @Override
    public void assignQuestionToSurvey(UUID surveyId, UUID questionId, UUID ignoredDepartmentId, UUID ignoredTeamId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id " + surveyId));
        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());

        UUID currentUserId = organizationContextUtil.getCurrentUserId();

        boolean isOwner = survey.getOwnerId().equals(currentUserId);
        boolean isOrgManager = organizationContextUtil.hasAuthority("ORG_MANAGER");
        boolean isDepartmentManager = organizationContextUtil.hasAuthority("DEPARTMENT_MANAGER");
        boolean isTeamManager = organizationContextUtil.hasAuthority("TEAM_MANAGER");

        if (!(isOwner || isOrgManager || isDepartmentManager || isTeamManager)) {
            throw new AccessDeniedException("Only owner, ORG_MANAGER, DEPARTMENT_MANAGER, or TEAM_MANAGER can assign questions.");
        }

        if (assignedQuestionService.isQuestionAssignedToSurvey(surveyId, questionId)) {
            throw new IllegalStateException("This question is already assigned to the survey.");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));


        UUID departmentId = organizationContextUtil.getCurrentDepartmentIdOrNull();
        UUID teamId = organizationContextUtil.getCurrentTeamIdOrNull();

        assignedQuestionService.assignQuestionToSurvey(surveyId, questionId, departmentId, teamId);
        updateSurveyLockStatus(survey);

    }

    @Override
    public void unassignQuestionFromSurvey(UUID surveyId, UUID assignedQuestionId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id " + surveyId));
        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());

        UUID currentUserId = organizationContextUtil.getCurrentUserId();

        boolean isOwner = survey.getOwnerId().equals(currentUserId);
        boolean isOrgManager = organizationContextUtil.hasAuthority("ORG_MANAGER");
        boolean isDepartmentManager = organizationContextUtil.hasAuthority("DEPARTMENT_MANAGER");
        boolean isTeamManager = organizationContextUtil.hasAuthority("TEAM_MANAGER");

        if (!(isOwner || isOrgManager || isDepartmentManager || isTeamManager)) {
            throw new AccessDeniedException("Only owner, ORG_MANAGER, DEPARTMENT_MANAGER, or TEAM_MANAGER can unassign questions.");
        }

        AssignedQuestion aq = assignedQuestionRepository.findById(assignedQuestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Assigned question not found"));

        // Vérifier que la question appartient bien au survey
        if (!aq.getSurvey().getSurveyId().equals(surveyId)) {
            throw new IllegalArgumentException("This assigned question does not belong to the given survey.");
        }

        // Vérifier le lock
        if (aq.getLocked() != null && aq.getLocked() && !currentUserId.equals(aq.getLockedBy())) {
            throw new LockedException("This question is locked by another user and cannot be unassigned.");
        }

        assignedQuestionRepository.delete(aq);
        updateSurveyLockStatus(survey);

    }


    @Override
    public List<SurveyDto> getSurveysByOrganization(UUID organizationId) {
        List<Survey> surveys = surveyRepository.findByOrganizationId(organizationId);
        return surveys.stream()
                .map(SurveyMapper::toSurveyDto)
                .collect(Collectors.toList());
    }


    @Override
    public SurveyDto lockSurvey(UUID id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id " + id));
        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());

        UUID currentUserId = organizationContextUtil.getCurrentUserId();

        boolean isOwner = survey.getOwnerId().equals(currentUserId);
        boolean isOrgManager = organizationContextUtil.hasAuthority("ORG_MANAGER");
        boolean isDepartmentManager = organizationContextUtil.hasAuthority("DEPARTMENT_MANAGER");
        boolean isTeamManager = organizationContextUtil.hasAuthority("TEAM_MANAGER");

        if (!(isOwner || isOrgManager || isDepartmentManager || isTeamManager)) {
            throw new AccessDeniedException("Only owner, ORG_MANAGER, DEPARTMENT_MANAGER, or TEAM_MANAGER can lock a survey.");
        }

        // Lock each assigned question if possible
        survey.getAssignedQuestions().forEach(aq -> {
            if ((aq.getLocked() == null || !aq.getLocked())
                    || currentUserId.equals(aq.getLockedBy())) {
                aq.setLocked(true);
                aq.setLockedAt(LocalDateTime.now());
                aq.setLockedBy(currentUserId);
                assignedQuestionRepository.save(aq);
            }
        });

        // Lock the survey only if all questions are locked
        boolean allQuestionsLocked = survey.getAssignedQuestions()
                .stream()
                .allMatch(aq -> aq.getLocked() != null && aq.getLocked());
        survey.setLocked(allQuestionsLocked);
        surveyRepository.save(survey);

        return SurveyMapper.toSurveyDto(survey);
    }

    @Override
    public SurveyDto unlockSurvey(UUID id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id " + id));
        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());

        UUID currentUserId = organizationContextUtil.getCurrentUserId();

        boolean isOwner = survey.getOwnerId().equals(currentUserId);
        boolean isOrgManager = organizationContextUtil.hasAuthority("ORG_MANAGER");
        boolean isDepartmentManager = organizationContextUtil.hasAuthority("DEPARTMENT_MANAGER");
        boolean isTeamManager = organizationContextUtil.hasAuthority("TEAM_MANAGER");

        if (!(isOwner || isOrgManager || isDepartmentManager || isTeamManager)) {
            throw new AccessDeniedException("Only owner, ORG_MANAGER, DEPARTMENT_MANAGER, or TEAM_MANAGER can unlock a survey.");
        }

        // Unlock questions locked by current user
        survey.getAssignedQuestions().forEach(aq -> {
            if (aq.getLocked() != null && aq.getLocked() && currentUserId.equals(aq.getLockedBy())) {
                aq.setLocked(false);
                aq.setLockedAt(null);
                aq.setLockedBy(null);
                assignedQuestionRepository.save(aq);
            }
        });

        // Survey is unlocked if at least one question is unlocked
        boolean allQuestionsLocked = survey.getAssignedQuestions()
                .stream()
                .allMatch(aq -> aq.getLocked() != null && aq.getLocked());
        survey.setLocked(allQuestionsLocked);
        surveyRepository.save(survey);

        return SurveyMapper.toSurveyDto(survey);
    }

    public boolean exists(UUID id) {
        return surveyRepository.existsById(id);
    }

    @Transactional
    @Override
    public SurveyDto publishSurvey(UUID surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id " + surveyId));

        organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());

        if (survey.getStatus() == SurveyStatus.CLOSED) {
            throw new BadRequestException("Closed surveys cannot be republished.");
        }

        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BadRequestException("Only surveys in DRAFT status can be published.");
        }

        if (survey.getAssignedQuestions() == null || survey.getAssignedQuestions().isEmpty()) {
            throw new BadRequestException("Survey must have at least one assigned question before publishing.");
        }

        survey.setStatus(SurveyStatus.ACTIVE);
        surveyRepository.save(survey);

        return SurveyMapper.toSurveyDto(survey);
    }

    @Override
    public List<SurveyDto> getActiveAndClosedSurveys() {
        UUID organizationId = organizationContextUtil.getCurrentOrganizationId();

        List<Survey> surveys = surveyRepository.findByOrganizationId(organizationId);

        List<Survey> accessibleSurveys = new ArrayList<>();
        for (Survey survey : surveys) {
            try {
                organizationContextUtil.validateOrganizationAccess(survey.getOrganizationId());
                updateSurveyStatusIfExpired(survey);
                if (survey.getStatus() == SurveyStatus.ACTIVE || survey.getStatus() == SurveyStatus.CLOSED) {
                    accessibleSurveys.add(survey);
                }
            } catch (Exception e) {
                // Ignore surveys auxquels l'utilisateur n'a pas accès
            }
        }

        return accessibleSurveys.stream()
                .map(SurveyMapper::toSurveyDto)
                .collect(Collectors.toList());
    }
}
