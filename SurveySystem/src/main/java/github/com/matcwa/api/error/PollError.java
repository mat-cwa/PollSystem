package github.com.matcwa.api.error;

import github.com.matcwa.infrastructure.error.ResponseError;

public enum PollError implements ResponseError {
    WRONG_NAME_ERROR("Invalid name", 400),
    AUTHORIZATION_ERROR(" You don’t have permission to access this resource", 403),
    USER_NOT_FOUND("User not found",404),
    POLL_NOT_FOUND_ERROR("Poll not exist", 404);
    private String message;
    private int httpCode;

    PollError(String message, int httpCode) {
        this.message = message;
        this.httpCode = httpCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }
}
