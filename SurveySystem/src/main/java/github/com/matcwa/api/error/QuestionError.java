package github.com.matcwa.api.error;

import github.com.matcwa.infrastructure.ResponseError;

public enum QuestionError implements ResponseError {
    EMPTY_CONTENT_ERROR("THE QUESTION CONTENT CANT BE EMPTY", 400),
    POLL_NOT_FOUND_ERROR("POLL DOESNT EXISTS", 404),
    QUESTION_NOT_FOUND_ERROR("QUESTION DOESNT EXISTS", 404),
    USER_NOT_FOUND_ERROR("USER TAKEN FROM TOKEN DOESNT EXIST",404),
    AUTHORIZATION_ERROR(" You don’t have permission to access this resource", 403);
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
