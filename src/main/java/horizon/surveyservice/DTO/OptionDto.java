package horizon.surveyservice.DTO;


import java.util.UUID;

public class OptionDto {
    private UUID optionId;
    private UUID questionId;
    private String optionText;
    private boolean correct;
    private boolean selected;
    private Long optionScore;

    public OptionDto(UUID optionId, UUID questionId, String optionText, Long optionScore, boolean correct,boolean selected) {
        this.optionId = optionId;
        this.questionId = questionId;
        this.optionText = optionText;
        this.optionScore = optionScore;
        this.correct = correct;
        this.selected = selected;
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

    public void setOptionScore(Long optionScore) {
        this.optionScore = optionScore;
    }

    public boolean isCorrect() {return correct;}

    public void setCorrect(boolean correct) { this.correct = correct;}

    public boolean isSelected() {return selected; }

    public void setSelected(boolean selected) {this.selected = selected; }
}
