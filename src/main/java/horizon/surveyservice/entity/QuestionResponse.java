package horizon.surveyservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name= "questionResponse")
public class QuestionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionResponseId;
    private Long questionId;
    @ManyToOne
    @JoinColumn(name= "surveyResponseId",referencedColumnName = "surveyResponseId")
    private SurveyResponse surveyResponse;
    private String questionText;
    @JsonManagedReference
    @OneToMany(mappedBy = "questionResponse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OptionResponse> optionResponses;
    private Long questionScore;
    private LocalDateTime submittedAt;

    public QuestionResponse(Long questionResponseId, LocalDateTime submittedAt, Long questionScore, List<OptionResponse> optionResponses, String questionText, Long questionId) {
        this.questionResponseId = questionResponseId;
        this.submittedAt = submittedAt;
        this.questionScore = questionScore;
        this.optionResponses = optionResponses;
        this.questionText = questionText;
        this.questionId = questionId;
    }
    public QuestionResponse() {}

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

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getQuestionResponseId() {
        return questionResponseId;
    }

    public void setQuestionResponseId(Long questionResponseId) {
        this.questionResponseId = questionResponseId;
    }
}
