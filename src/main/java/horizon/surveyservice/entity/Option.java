package horizon.surveyservice.entity;


import jakarta.persistence.*;

//import java.util.UUID;


@Entity
@Table(name = "option")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long optionId;
//    @Id
//    @GeneratedValue
//    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
//    private UUID optionId;
    @ManyToOne
    @JoinColumn(name= "questionId",referencedColumnName = "questionId")
    private Question question;
    @Column(nullable = false)
    private String optionText;
    @Column(nullable = false)
    private boolean isCorrect;
    @Column(nullable = false)
    private Long optionScore;
    @Column(nullable = false)
    private boolean isLocked;

    public Option(Long optionId, Question question, String optionText, boolean isCorrect, Long optionScore, boolean isLocked) {
        this.optionId = optionId;
        this.question = question;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.optionScore = optionScore;
        this.isLocked = isLocked;
    }
    public Option() {
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Question getQuestion() {
        return question;
    }

    public String getOptionText() {
        return optionText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public Long getOptionScore() {
        return optionScore;
    }

    public boolean isLocked() {
        return isLocked;
    }



    public void setQuestion(Question question) {
        this.question = question;
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
