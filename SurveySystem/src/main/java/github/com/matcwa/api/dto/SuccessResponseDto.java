package github.com.matcwa.api.dto;

public class SuccessResponseDto {
    private String response;

    public SuccessResponseDto() {
    }

    public SuccessResponseDto(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
