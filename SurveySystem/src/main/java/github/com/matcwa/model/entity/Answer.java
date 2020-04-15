package github.com.matcwa.model.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ANSWER", schema = "POLL")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String answerDescription;
    @OneToMany(mappedBy = "answer", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Vote> votes = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "fk_question")
    private Question question;

    @ElementCollection
    @CollectionTable(name = "answer_ipset",
            joinColumns = @JoinColumn(name = "fk_answer"),
            schema = "POLL")
    @JoinColumn(name = "ip_set")
    private Set<String> ipSet = new HashSet<>();

    public Answer() {
    }

    public Answer(String answerDescription, Set<Vote> votes, Question question) {
        this.answerDescription = answerDescription;
        this.votes = votes;
        this.question = question;
    }

    public Answer(String answerDescription, Question question) {
        this.answerDescription = answerDescription;
        this.question = question;
    }

    public void addVote(Vote vote) {
        votes.add(vote);
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

    public String getAnswerDescription() {
        return answerDescription;
    }

    public void setAnswerDescription(String answerDescription) {
        this.answerDescription = answerDescription;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
        if (!(o instanceof Answer)) return false;
        Answer answer = (Answer) o;
        return Objects.equals(getId(), answer.getId()) &&
                Objects.equals(getAnswerDescription(), answer.getAnswerDescription()) &&
                Objects.equals(getVotes(), answer.getVotes()) &&
                Objects.equals(getQuestion(), answer.getQuestion());
    }

}
