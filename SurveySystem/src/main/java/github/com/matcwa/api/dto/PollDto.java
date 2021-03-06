package github.com.matcwa.api.dto;

import com.fasterxml.jackson.annotation.*;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.api.dto.UserDto;

import java.util.Objects;
import java.util.Set;

public class PollDto {
    private Long id;
    private String name;
    @JsonManagedReference
    private UserDto owner;
    @JsonManagedReference(value = "poll-question")
    private Set<QuestionDto> questions;

    public PollDto() {
    }

    public PollDto(Long id, String name, UserDto owner, Set<QuestionDto> questions) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.questions = questions;
    }

    public PollDto(String name, Set<QuestionDto> questions) {
        this.name = name;
        this.questions = questions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public Set<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<QuestionDto> questions) {
        this.questions = questions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PollDto)) return false;
        PollDto pollDto = (PollDto) o;
        return Objects.equals(getId(), pollDto.getId()) &&
                Objects.equals(getName(), pollDto.getName()) &&
                Objects.equals(getOwner(), pollDto.getOwner()) &&
                Objects.equals(getQuestions(), pollDto.getQuestions());
    }

}
