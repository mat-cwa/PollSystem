package github.com.matcwa.api.error;

import github.com.matcwa.infrastructure.ResponseError;

public enum QuestionError implements ResponseError {
    EMPTY_CONTENT_ERROR("THE QUESTION CONTENT CANT BE EMPTY", 400),
    POLL_NOT_FOUND_ERROR("Poll not exist", 404),
    EMPTY_ANSWERS_ERROR("THE LIST OF ANSWERS CANT BE EMPTY", 400);

    private String message;
    private int httpCode;

    QuestionError(String message, int httpCode) {
        this.message = message;
        this.httpCode = httpCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getHttpCode() {
        return httpCode;
    }
}
