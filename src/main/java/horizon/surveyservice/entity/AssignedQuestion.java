package horizon.surveyservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "assigned_questions")
@Entity
public class AssignedQuestion {
    @Id
    @GeneratedValue
    private UUID assignedQuestionId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "surveyId", nullable = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;
    private UUID departmentId;
    private UUID teamId;
    private LocalDateTime assignedAt;
    private Boolean locked;
    private LocalDateTime lockedAt;
    private UUID lockedBy;

    public AssignedQuestion() {
    }
    public UUID getAssignedQuestionId() {
        return assignedQuestionId;
    }

    public void setAssignedQuestionId(UUID assignedQuestionId) {
        this.assignedQuestionId = assignedQuestionId;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
