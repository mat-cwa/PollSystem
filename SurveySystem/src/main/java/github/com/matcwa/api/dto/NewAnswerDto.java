package github.com.matcwa.api.dto;

public class NewAnswerDto {
    private String description;

    public NewAnswerDto() {
    }

    public NewAnswerDto(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
