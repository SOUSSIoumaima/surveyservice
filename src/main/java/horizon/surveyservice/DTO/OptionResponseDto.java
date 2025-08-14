package horizon.surveyservice.DTO;

import java.util.UUID;

public class OptionResponseDto {
    private UUID optionResponseId;
    private UUID optionId;
    private UUID questionResponseId;
    private String optionText;
    private boolean correct;
    private boolean selected;
    private Long optionScore;

    public OptionResponseDto(UUID optionResponseId, UUID optionId, String optionText, boolean correct, boolean selected, Long optionScore) {
        this.optionResponseId = optionResponseId;
        this.optionId = optionId;
        this.optionText = optionText;
        this.correct = correct;
        this.selected = selected;
        this.optionScore = optionScore;
    }

    public OptionResponseDto()
    {}

    public UUID getQuestionResponseId() {
        return questionResponseId;
    }

    public void setQuestionResponseId(UUID questionResponseId) {
        this.questionResponseId = questionResponseId;
    }

    public UUID getOptionResponseId() {
        return optionResponseId;
    }

    public void setOptionResponseId(UUID optionResponseId) {
        this.optionResponseId = optionResponseId;
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

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getOptionScore() {
        return optionScore;
    }

    public void setOptionScore(Long optionScore) {
        this.optionScore = optionScore;
    }
}
