package horizon.surveyservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name= "optionResponse")
public class OptionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionResponseId;
    @Column(nullable = false)
    private Long optionId;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name= "questionResponseId",referencedColumnName = "questionResponseId")
    private QuestionResponse questionResponse;
    @Column(nullable = false)
    private String optionText;
    @Column(nullable = false)
    private boolean isCorrect;
    @Column(nullable = false)
    private boolean isSelected;
    @Column(nullable = false)
    private Long optionScore;

    public OptionResponse(Long optionResponseId, QuestionResponse questionResponse, String optionText, boolean isCorrect, boolean isSelected, Long optionScore) {
        this.optionResponseId = optionResponseId;
        this.questionResponse = questionResponse;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.isSelected = isSelected;
        this.optionScore = optionScore;
    }
    public OptionResponse() {
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Long getOptionResponseId() {
        return optionResponseId;
    }

    public void setOptionResponseId(Long optionResponseId) {
        this.optionResponseId = optionResponseId;
    }

    public QuestionResponse getQuestionResponse() {
        return questionResponse;
    }

    public void setQuestionResponse(QuestionResponse questionResponse) {
        this.questionResponse = questionResponse;
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
