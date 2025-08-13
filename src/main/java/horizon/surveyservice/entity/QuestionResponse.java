package horizon.surveyservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "question_response")
public class QuestionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID questionResponseId;
    private UUID questionId;
    @ManyToOne
    @JoinColumn(name = "surveyResponseId", referencedColumnName = "surveyResponseId")
    private SurveyResponse surveyResponse;
    private String questionText;
    @OneToMany(mappedBy = "questionResponse", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("questionResponse")
    private List<OptionResponse> optionResponses;
    private Long questionScore;
    private LocalDateTime submittedAt;

    public QuestionResponse(UUID questionResponseId, LocalDateTime submittedAt, Long questionScore, List<OptionResponse> optionResponses, String questionText, UUID questionId) {
        this.questionResponseId = questionResponseId;
        this.submittedAt = submittedAt;
        this.questionScore = questionScore;
        this.optionResponses = optionResponses;
        this.questionText = questionText;
        this.questionId = questionId;
    }
    public QuestionResponse() {
        this.submittedAt = LocalDateTime.now();
    }
    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    public SurveyResponse getSurveyResponse() {
        return surveyResponse;
    }

    public void setSurveyResponse(SurveyResponse surveyResponse) {
        this.surveyResponse = surveyResponse;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Long getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(Long questionScore) {
        this.questionScore = questionScore;
    }

    public List<OptionResponse> getOptionResponses() {
        return optionResponses;
    }

    public void setOptionResponses(List<OptionResponse> optionResponses) {
        this.optionResponses = optionResponses;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public UUID getQuestionResponseId() {
        return questionResponseId;
    }

    public void setQuestionResponseId(UUID questionResponseId) {
        this.questionResponseId = questionResponseId;
    }
}
