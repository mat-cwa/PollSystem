package github.com.matcwa.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User owner;
    @ManyToOne
    private Answer answer;

    public Vote() {
    }

    public Vote(Answer answer) {
        this.answer = answer;
        date=new Date();
    }

    private Date date = new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vote)) return false;
        Vote vote = (Vote) o;
        return Objects.equals(getId(), vote.getId()) &&
                Objects.equals(getOwner(), vote.getOwner()) &&
                Objects.equals(getAnswer(), vote.getAnswer()) &&
                Objects.equals(date, vote.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner(), getAnswer(), date);
    }
}
