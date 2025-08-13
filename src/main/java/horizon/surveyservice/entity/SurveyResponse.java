package horizon.surveyservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "survey_response")
public class SurveyResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID surveyResponseId;
    private UUID surveyId;
    private UUID respondentId;

    @OneToMany(mappedBy = "surveyResponse", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionResponse> questionResponses;
    private LocalDateTime submittedAt;
    private Long totalScore;
    private boolean isFinal;

    public SurveyResponse(UUID surveyResponseId, UUID surveyId, List<QuestionResponse> questionResponses, LocalDateTime submittedAt, Long totalScore, boolean isFinal) {
        this.surveyResponseId = surveyResponseId;
        this.surveyId = surveyId;
        this.questionResponses = questionResponses;
        this.submittedAt = submittedAt;
        this.totalScore = totalScore;
        this.isFinal = isFinal;
    }

    public SurveyResponse() {
        this.submittedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    public UUID getSurveyResponseId() {
        return surveyResponseId;
    }

    public void setSurveyResponseId(UUID surveyResponseId) {
        this.surveyResponseId = surveyResponseId;
    }

    public UUID getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(UUID surveyId) {
        this.surveyId = surveyId;
    }

    public UUID getRespondentId() {
        return respondentId;
    }

    public void setRespondentId(UUID respondentId) {
        this.respondentId = respondentId;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public List<QuestionResponse> getQuestionResponses() {
        return questionResponses;
    }

    public void setQuestionResponses(List<QuestionResponse> questionResponses) {
        this.questionResponses = questionResponses;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Long getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }
}
