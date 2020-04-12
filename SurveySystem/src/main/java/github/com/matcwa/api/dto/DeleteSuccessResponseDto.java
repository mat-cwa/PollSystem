package github.com.matcwa.api.dto;

public class DeleteSuccessResponseDto {
    private String response;

    public DeleteSuccessResponseDto() {
    }

    public DeleteSuccessResponseDto(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
