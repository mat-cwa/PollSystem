package github.com.matcwa.model.entity;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "QUESTION", schema = "POLL")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String questionDescription;
    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Answer> answers = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "fk_poll")
    private Poll poll;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "question_ipset",
            joinColumns = @JoinColumn(name = "fk_question"),
            schema = "POLL")
    @JoinColumn(name = "ip_set")
    private Set<String> ipSet = new HashSet<>();

    public Question(String questionDescription, Set<Answer> answers, Poll poll) {
        this.questionDescription = questionDescription;
        this.answers = answers;
        this.poll = poll;
    }

    public Question(String questionDescription, Poll poll) {
        this.questionDescription = questionDescription;
        this.poll = poll;
    }

    public Question() {
    }
    public void addIpAddress(String ipAddress){
        ipSet.add(ipAddress);
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

    public Set<String> getIpSet() {
        return ipSet;
    }

    public void setIpSet(Set<String> ipSet) {
        this.ipSet = ipSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question question = (Question) o;
        return Objects.equals(getId(), question.getId()) &&
                Objects.equals(getQuestionDescription(), question.getQuestionDescription()) &&
                Objects.equals(getAnswers(), question.getAnswers()) &&
                Objects.equals(getPoll(), question.getPoll());
    }
}
