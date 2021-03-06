package github.com.matcwa.api.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import github.com.matcwa.model.enums.Role;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class UserDto {
    private Long id;
    private String username;
    private Role role;
    private String token;
    private String email;
    private boolean isActive;
    private Date dateOfRegistration;
    @JsonBackReference
    private Set<PollDto> pollSet;
    @JsonManagedReference(value = "user-vote")
    private Set<VoteDto> votes;

    public UserDto(Long id, String username, Set<PollDto> pollSet, Set<VoteDto> votes) {
        this.id = id;
        this.username = username;
        this.pollSet = pollSet;
        this.votes = votes;
    }

    public UserDto() {
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

    public Set<PollDto> getPollSet() {
        return pollSet;
    }

    public void setPollSet(Set<PollDto> pollSet) {
        this.pollSet = pollSet;
    }

    public Set<VoteDto> getVotes() {
        return votes;
    }

    public void setVotes(Set<VoteDto> votes) {
        this.votes = votes;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto)) return false;
        UserDto userDto = (UserDto) o;
        return isActive() == userDto.isActive() &&
                Objects.equals(getId(), userDto.getId()) &&
                Objects.equals(getUsername(), userDto.getUsername()) &&
                Objects.equals(getRole(), userDto.getRole()) &&
                Objects.equals(getEmail(), userDto.getEmail()) &&
                Objects.equals(getPollSet(), userDto.getPollSet()) &&
                Objects.equals(getVotes(), userDto.getVotes());
    }

}
