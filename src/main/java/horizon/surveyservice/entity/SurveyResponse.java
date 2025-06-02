package horizon.surveyservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
public class SurveyResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyResponseId;
    private Long surveyId;
    private Long respondentId;
    @JsonBackReference
    @OneToMany(mappedBy = "surveyResponse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuestionResponse> questionResponses;
    private LocalDateTime submittedAt;
    private Long totalScore;

    public SurveyResponse(Long surveyResponseId, Long surveyId, Long respondentId, List<QuestionResponse> questionResponses, LocalDateTime submittedAt, Long totalScore) {
        this.surveyResponseId = surveyResponseId;
        this.surveyId = surveyId;
        this.respondentId = respondentId;
        this.questionResponses = questionResponses;
        this.submittedAt = submittedAt;
        this.totalScore = totalScore;
    }

    public SurveyResponse() {}

    public Long getSurveyResponseId() {
        return surveyResponseId;
    }

    public void setSurveyResponseId(Long surveyResponseId) {
        this.surveyResponseId = surveyResponseId;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getRespondentId() {
        return respondentId;
    }

    public void setRespondentId(Long respondentId) {
        this.respondentId = respondentId;
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
