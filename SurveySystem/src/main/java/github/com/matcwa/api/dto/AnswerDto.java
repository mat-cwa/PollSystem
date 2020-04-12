package github.com.matcwa.api.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.api.dto.VoteDto;

import java.util.Objects;
import java.util.Set;

public class AnswerDto {
    private Long id;
    private String answerDescription;
    @JsonManagedReference(value = "answer-vote")
    private Set<VoteDto> votes;
    @JsonBackReference(value = "question-answer")
    private QuestionDto question;

    public AnswerDto() {
    }

    public AnswerDto(Long id, String answerDescription, Set<VoteDto> votes, QuestionDto question) {
        this.id = id;
        this.answerDescription = answerDescription;
        this.votes = votes;
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswerDescription() {
        return answerDescription;
    }

    public void setAnswerDescription(String answerDescription) {
        this.answerDescription = answerDescription;
    }

    public Set<VoteDto> getVotes() {
        return votes;
    }

    public void setVotes(Set<VoteDto> votes) {
        this.votes = votes;
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerDto)) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return Objects.equals(getId(), answerDto.getId()) &&
                Objects.equals(getAnswerDescription(), answerDto.getAnswerDescription()) &&
                Objects.equals(getVotes(), answerDto.getVotes()) &&
                Objects.equals(getQuestion(), answerDto.getQuestion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAnswerDescription(), getQuestion());
    }
}
