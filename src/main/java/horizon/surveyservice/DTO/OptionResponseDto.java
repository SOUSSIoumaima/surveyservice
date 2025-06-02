package horizon.surveyservice.DTO;

public class OptionResponseDto {
    private Long optionResponseId;
    private Long optionId;
    private Long questionResponseId;
    private String optionText;
    private boolean isCorrect;
    private boolean isSelected;
    private Long optionScore;

    public OptionResponseDto(Long optionResponseId, Long optionId, String optionText, boolean isCorrect, boolean isSelected, Long optionScore) {
        this.optionResponseId = optionResponseId;
        this.optionId = optionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.isSelected = isSelected;
        this.optionScore = optionScore;
    }

    public OptionResponseDto()
    {}

    public Long getQuestionResponseId() {
        return questionResponseId;
    }

    public void setQuestionResponseId(Long questionResponseId) {
        this.questionResponseId = questionResponseId;
    }

    public Long getOptionResponseId() {
        return optionResponseId;
    }

    public void setOptionResponseId(Long optionResponseId) {
        this.optionResponseId = optionResponseId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
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
