package github.com.matcwa.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String answerDescription;
    @OneToMany(mappedBy = "answer",fetch = FetchType.LAZY)
    private Set<Vote> votes;
    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> ipSet =new HashSet<>();


    public Answer() {
    }

    public Answer(String answerDescription, Set<Vote> votes, Question question) {
        this.answerDescription = answerDescription;
        this.votes = votes;
        this.question = question;
    }

    public void addVote(Vote vote,String ipAddress){
        votes.add(vote);
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
}
