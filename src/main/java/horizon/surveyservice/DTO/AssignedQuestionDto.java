package horizon.surveyservice.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssignedQuestionDto {
    private UUID assignedQuestionId;

    private UUID surveyId;

    private UUID questionId;

    private UUID departmentId;

    private UUID teamId;

    private LocalDateTime assignedAt;
    private Boolean locked;
    private LocalDateTime lockedAt;
    private UUID lockedBy;

    // 👉 Getters et Setters

    public UUID getAssignedQuestionId() {
        return assignedQuestionId;
    }

    public void setAssignedQuestionId(UUID assignedQuestionId) {
        this.assignedQuestionId = assignedQuestionId;
    }

    public UUID getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(UUID surveyId) {
        this.surveyId = surveyId;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }

    public UUID getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(UUID lockedBy) {
        this.lockedBy = lockedBy;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
