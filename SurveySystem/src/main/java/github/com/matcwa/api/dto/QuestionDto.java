package github.com.matcwa.api.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Set;

public class QuestionDto {
    private Long id;
    private String questionDescription;
    @JsonManagedReference(value = "question-answer")
    private Set<AnswerDto> answers;
    @JsonBackReference(value = "poll-question")
    private PollDto poll;

    public QuestionDto() {
    }

    public QuestionDto(Long id, String questionDescription, Set<AnswerDto> answers) {
        this.id = id;
        this.questionDescription = questionDescription;
        this.answers = answers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public Set<AnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<AnswerDto> answers) {
        this.answers = answers;
    }

    public PollDto getPoll() {
        return poll;
    }

    public void setPoll(PollDto poll) {
        this.poll = poll;
    }
}
