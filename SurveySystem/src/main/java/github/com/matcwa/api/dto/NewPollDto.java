package github.com.matcwa.api.dto;

public class NewPollDto {
private String name;

    public NewPollDto() {
    }

    public NewPollDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
