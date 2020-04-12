package github.com.matcwa.api.error;

import github.com.matcwa.infrastructure.ResponseError;

public enum  AnswerError implements ResponseError {
    WRONG_NAME_ERROR("Invalid name", 400),
    QUESTION_NOT_FOUND_ERROR("Question not exist", 404),
    ANSWER_NOT_FOUND_ERROR("Answer not exist", 404),
    USER_NOT_FOUND_ERROR("USER TAKEN FROM TOKEN DOESNT EXIST",404),
    ONE_VOTE_PER_IP_ERROR("One vote per IP allowed", 400),
    AUTHORIZATION_ERROR(" You don’t have permission to access this resource", 403);

    private String message;
    private int httpCode;

    AnswerError(String message, int httpCode) {
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
