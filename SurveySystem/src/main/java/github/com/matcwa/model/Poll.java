package github.com.matcwa.model;

import javax.persistence.*;
import java.util.HashSet;
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



}
