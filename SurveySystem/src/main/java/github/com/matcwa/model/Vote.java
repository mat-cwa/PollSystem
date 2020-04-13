package github.com.matcwa.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "VOTE", schema = "POLL")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "fk_user")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "fk_answer")
    private Answer answer;

    private Date date;

    public Vote() {
        date = new Date();
    }

    public Vote(Answer answer) {
        this.answer = answer;
        date = new Date();
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

}
