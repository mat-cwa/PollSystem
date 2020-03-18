package github.com.matcwa.api.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Date;

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
}
