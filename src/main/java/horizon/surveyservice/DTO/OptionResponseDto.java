package horizon.surveyservice.DTO;

import java.util.UUID;

public class OptionResponseDto {
    private UUID optionResponseId;
    private UUID optionId;
    private UUID questionResponseId;
    private String optionText;
    private boolean isCorrect;
    private boolean isSelected;
    private Long optionScore;

    public OptionResponseDto(UUID optionResponseId, UUID optionId, String optionText, boolean isCorrect, boolean isSelected, Long optionScore) {
        this.optionResponseId = optionResponseId;
        this.optionId = optionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.isSelected = isSelected;
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
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Long getOptionScore() {
        return optionScore;
    }

    public void setOptionScore(Long optionScore) {
        this.optionScore = optionScore;
    }
}
