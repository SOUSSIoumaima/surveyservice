package horizon.surveyservice.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SurveyResponseDto {
    private UUID surveyResponseId;
    private UUID surveyId;
    private UUID respondentId;
    private LocalDateTime submittedAt;
    private Long totalScore;
    private List<QuestionResponseDto> questionResponses;


    public SurveyResponseDto(UUID surveyResponseId, UUID surveyId, UUID respondentId, LocalDateTime submittedAt, Long totalScore, List<QuestionResponseDto> questionResponses) {
        this.surveyResponseId = surveyResponseId;
        this.surveyId = surveyId;
        this.respondentId = respondentId;
        this.submittedAt = submittedAt;
        this.totalScore = totalScore;
        this.questionResponses = questionResponses;
    }
    public SurveyResponseDto() {}

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
