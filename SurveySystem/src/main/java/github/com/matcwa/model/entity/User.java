package github.com.matcwa.model.entity;

import github.com.matcwa.model.enums.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "USER", schema = "POLL")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Email
    private String email;
    private boolean isActive;
    private Date dateOfRegistration;
    @Enumerated(EnumType.STRING)
    private Role role=Role.USER;
    @OneToMany(mappedBy = "owner")
    private Set<Poll> pollSet = new HashSet<>();
    @OneToMany(mappedBy = "owner")
    private Set<Vote> votes;

    public User() {
        this.dateOfRegistration = new Date();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.dateOfRegistration = new Date();
    }

    public User(String username, String password, @Email String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.dateOfRegistration = new Date();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void addPoll(Poll poll) {
        pollSet.add(poll);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(Date dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return isActive() == user.isActive() &&
                Objects.equals(getId(), user.getId()) &&
                Objects.equals(getUsername(), user.getUsername()) &&
                Objects.equals(getEmail(), user.getEmail()) &&
                getRole() == user.getRole() &&
                Objects.equals(getPollSet(), user.getPollSet()) &&
                Objects.equals(getVotes(), user.getVotes());
    }
}
