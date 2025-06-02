package horizon.surveyservice.DTO;



public class OptionDto {
    private Long optionId;
    private Long questionId;
    private String optionText;
    private boolean isCorrect;
    private Long optionScore;
    private boolean isLocked;

    public OptionDto(Long optionId, Long questionId, String optionText, Long optionScore, boolean isCorrect, boolean isLocked) {
        this.optionId = optionId;
        this.questionId = questionId;
        this.optionText = optionText;
        this.optionScore = optionScore;
        this.isCorrect = isCorrect;
        this.isLocked = isLocked;
    }
    public OptionDto() {
    }



    public Long getOptionId() {
        return optionId;
    }

    public Long getQuestionId() {
        return questionId;
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

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public void setQuestionId(Long questionId) {this.questionId = questionId;}

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
