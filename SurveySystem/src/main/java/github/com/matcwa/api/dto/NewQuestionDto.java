package github.com.matcwa.api.dto;

public class NewQuestionDto {

    private String description;

    public NewQuestionDto() {
    }

    public NewQuestionDto(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
