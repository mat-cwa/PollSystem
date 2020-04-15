package github.com.matcwa.model.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "POLL", schema = "POLL")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_user")
    private User owner;
    @OneToMany(cascade = {CascadeType.REMOVE,CascadeType.PERSIST}, mappedBy = "poll")
    private Set<Question> questions=new HashSet<>();
    private boolean manyVotePerQuestion;

    public Poll() {
    }

    public Poll(String name, Set<Question> questions) {
        this.name = name;
        this.questions = questions;
    }
    public Poll(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public boolean isManyVotePerQuestion() {
        return manyVotePerQuestion;
    }

    public void setManyVotePerQuestion(boolean manyVotePerQuestion) {
        this.manyVotePerQuestion = manyVotePerQuestion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Poll)) return false;
        Poll poll = (Poll) o;
        return Objects.equals(getId(), poll.getId()) &&
                Objects.equals(getName(), poll.getName()) &&
                Objects.equals(getOwner(), poll.getOwner()) &&
                Objects.equals(getQuestions(), poll.getQuestions());
    }


}
