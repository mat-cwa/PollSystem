package github.com.matcwa.api.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Set;

public class UserDto {
    private Long id;
    private String username;
    @JsonManagedReference
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
}
