package github.com.matcwa.api.error;

import github.com.matcwa.infrastructure.ResponseError;

public enum PollError implements ResponseError {
    WRONG_NAME_ERROR("Invalid name", 400),
    NO_QUESTIONS_ERROR("The list of questions cant be empty", 400),
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
