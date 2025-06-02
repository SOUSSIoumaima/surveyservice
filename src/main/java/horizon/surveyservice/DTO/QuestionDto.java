package horizon.surveyservice.DTO;

import horizon.surveyservice.entity.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class QuestionDto {
    private Long questionId;
    private String subject;
    private String questionText;
    private QuestionType questionType;
    private boolean locked;
    private List<OptionDto> options;

    public QuestionDto(Long questionId, String subject, String questionText, QuestionType questionType, boolean locked, List<OptionDto> options) {
        this.questionId = questionId;
        this.subject = subject;
        this.questionText = questionText;
        this.questionType = questionType;
        this.locked = locked;
        this.options = options;
    }

    public QuestionDto() {
    }
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }
}
