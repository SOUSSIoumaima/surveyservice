package horizon.surveyservice.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class QuestionResponseDto {
    private UUID questionResponseId;
    private UUID questionId;
    private UUID surveyResponseId;
    private String questionText;
    private Long questionScore;
    private LocalDateTime submittedAt;
    private List<OptionResponseDto> optionResponses;

    public QuestionResponseDto(UUID questionResponseId, UUID questionId, UUID surveyResponseId, String questionText, Long questionScore, LocalDateTime submittedAt, List<OptionResponseDto> optionResponses) {
        this.questionResponseId = questionResponseId;
        this.questionId = questionId;
        this.surveyResponseId = surveyResponseId;
        this.questionText = questionText;
        this.questionScore = questionScore;
        this.submittedAt = submittedAt;
        this.optionResponses = optionResponses;
    }

    public QuestionResponseDto() {}

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

    public UUID getSurveyResponseId() {
        return surveyResponseId;
    }

    public void setSurveyResponseId(UUID surveyId) {
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
