package github.com.matcwa.model;


import javax.persistence.*;
import java.util.Set;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String questionDescription;
    @OneToMany(mappedBy = "question",cascade = CascadeType.ALL)
    private Set<Answer> answers;
    @ManyToOne
    private Poll poll;

    public Question(String questionDescription, Set<Answer> answers, Poll poll) {
        this.questionDescription = questionDescription;
        this.answers = answers;
        this.poll = poll;
    }

    public Question(String questionDescription, Set<Answer> answers) {
        this.questionDescription = questionDescription;
        this.answers=answers;
    }

    public Question() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public void addAnswer(Answer answer) {
    answers.add(answer);
    }
}
