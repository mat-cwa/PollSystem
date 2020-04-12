package github.com.matcwa.api.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Date;
import java.util.Objects;

public class VoteDto {
    private Long id;
    @JsonBackReference(value = "user-vote")
    private UserDto owner;
    @JsonBackReference(value = "answer-vote")
    private AnswerDto answer;
    private Date date;

    public VoteDto() {
    }

    public VoteDto(Long id, UserDto owner, AnswerDto answer) {
        this.id = id;
        this.owner = owner;
        this.answer = answer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public AnswerDto getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerDto answer) {
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
        if (!(o instanceof VoteDto)) return false;
        VoteDto voteDto = (VoteDto) o;
        return Objects.equals(getId(), voteDto.getId()) &&
                Objects.equals(getOwner(), voteDto.getOwner()) &&
                Objects.equals(getDate(), voteDto.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner(), getDate());
    }
}
