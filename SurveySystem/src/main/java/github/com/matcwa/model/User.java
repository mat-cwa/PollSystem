package github.com.matcwa.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @OneToMany(mappedBy = "owner")
    private Set<Poll> pollSet;
    @OneToMany(mappedBy = "owner")
    private Set<Vote> votes;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public Set<Poll> getPollSet() {
        return pollSet;
    }

    public void setPollSet(Set<Poll> pollSet) {
        this.pollSet = pollSet;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }
}
