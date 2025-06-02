package horizon.surveyservice.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyResponseDto {
    private Long surveyResponseId;
    private Long surveyId;
    private Long respondentId;
    private LocalDateTime submittedAt;
    private Long totalScore;
    private List<QuestionResponseDto> questionResponses;


    public SurveyResponseDto(Long surveyResponseId, Long surveyId, Long respondentId, LocalDateTime submittedAt, Long totalScore, List<QuestionResponseDto> questionResponses) {
        this.surveyResponseId = surveyResponseId;
        this.surveyId = surveyId;
        this.respondentId = respondentId;
        this.submittedAt = submittedAt;
        this.totalScore = totalScore;
        this.questionResponses = questionResponses;
    }
    public SurveyResponseDto() {}

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

    public List<QuestionResponseDto> getQuestionResponses() {
        return questionResponses;
    }

    public void setQuestionResponses(List<QuestionResponseDto> questionResponses) {
        this.questionResponses = questionResponses;
    }
}
