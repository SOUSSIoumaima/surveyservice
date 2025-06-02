package horizon.surveyservice.DTO;


import java.util.UUID;

public class OptionDto {
    private UUID optionId;
    private UUID questionId;
    private String optionText;
    private boolean isCorrect;
    private Long optionScore;
    private boolean isLocked;

    public OptionDto(UUID optionId, UUID questionId, String optionText, Long optionScore, boolean isCorrect, boolean isLocked) {
        this.optionId = optionId;
        this.questionId = questionId;
        this.optionText = optionText;
        this.optionScore = optionScore;
        this.isCorrect = isCorrect;
        this.isLocked = isLocked;
    }
    public OptionDto() {
    }


    public UUID getOptionId() {
        return optionId;
    }

    public void setOptionId(UUID optionId) {
        this.optionId = optionId;
    }


    public String getOptionText() {
        return optionText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public boolean isLocked() {return isLocked;}

    public Long getOptionScore() {
        return optionScore;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public void setOptionScore(Long optionScore) {
        this.optionScore = optionScore;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
