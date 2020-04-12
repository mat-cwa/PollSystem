package github.com.matcwa.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST})
    private User owner;
    @OneToMany(cascade = {CascadeType.REMOVE,CascadeType.PERSIST}, mappedBy = "poll")
    private Set<Question> questions=new HashSet<>();

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

    public Question addQuestion(Question question) {
        questions.add(question);
        return question;
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

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getOwner(), getQuestions());
    }
}
