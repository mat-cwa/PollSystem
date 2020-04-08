package github.com.matcwa.api.error;

import github.com.matcwa.infrastructure.ResponseError;

public enum UserError implements ResponseError {
    USER_NOT_FOUND_ERROR("User not exist", 404),
    WRONG_PASSWORD_ERROR("Wrong password", 400),
    EMPTY_USERNAME_ERROR("Username cant be empty",400),
    EMPTY_PASSWORD_ERROR("Username cant be empty",400),
    EMPTY_USERNAME_AND_PASSWORD_ERROR("Username and password cant be empty",400);
    private String message;
    private int httpCode;

    UserError(String message, int httpCode) {
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

