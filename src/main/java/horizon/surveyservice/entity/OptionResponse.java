package horizon.surveyservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "option_response")
public class OptionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID optionResponseId;
//    @Column(nullable = false)
    private UUID optionId;
    @ManyToOne
    @JoinColumn(name = "questionResponseId")
    @JsonIgnoreProperties("optionResponses")
    private QuestionResponse questionResponse;
    @Column(nullable = false)
    private String optionText;
    @Column(nullable = false)
    private boolean isCorrect;
    @Column(nullable = false)
    private boolean isSelected;
    @Column(nullable = false)
    private Long optionScore;

    public OptionResponse(UUID optionResponseId, QuestionResponse questionResponse, String optionText, boolean isCorrect, boolean isSelected, Long optionScore) {
        this.optionResponseId = optionResponseId;
        this.questionResponse = questionResponse;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.isSelected = isSelected;
        this.optionScore = optionScore;
    }
    public OptionResponse() {
    }

    public UUID getOptionId() {
        return optionId;
    }

    public void setOptionId(UUID optionId) {
        this.optionId = optionId;
    }

    public UUID getOptionResponseId() {
        return optionResponseId;
    }

    public void setOptionResponseId(UUID optionResponseId) {
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
