package horizon.surveyservice.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class QuestionResponseDto {
    private Long questionResponseId;
    private Long questionId;
    private Long surveyResponseId;
    private String questionText;
    private Long questionScore;
    private LocalDateTime submittedAt;
    private List<OptionResponseDto> optionResponses;

    public QuestionResponseDto(Long questionResponseId, Long questionId, String questionText, Long surveyId, Long questionScore, LocalDateTime submittedAt, List<OptionResponseDto> optionResponses) {
        this.questionResponseId = questionResponseId;
        this.questionId = questionId;
        this.questionText = questionText;
        this.surveyResponseId = surveyId;
        this.questionScore = questionScore;
        this.submittedAt = submittedAt;
        this.optionResponses = optionResponses;
    }

    public QuestionResponseDto() {}

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

    public Long getSurveyResponseId() {
        return surveyResponseId;
    }

    public void setSurveyResponseId(Long surveyId) {
        this.surveyResponseId = surveyId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Long getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(Long questionScore) {
        this.questionScore = questionScore;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<OptionResponseDto> getOptionResponses() {
        return optionResponses;
    }

    public void setOptionResponses(List<OptionResponseDto> optionResponses) {
        this.optionResponses = optionResponses;
    }
}
