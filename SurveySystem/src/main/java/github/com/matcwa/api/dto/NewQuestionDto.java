package github.com.matcwa.api.dto;

import github.com.matcwa.api.dto.AnswerDto;
import github.com.matcwa.api.dto.PollDto;

import java.util.Set;

public class NewQuestionDto {

    private String questionDescription;

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }
}
