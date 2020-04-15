package github.com.matcwa.api.error;

import github.com.matcwa.infrastructure.ResponseError;

public enum UserError implements ResponseError {
    USER_NOT_FOUND_ERROR("User not exist", 404),
    WRONG_PASSWORD_ERROR("Wrong password", 400),
    USER_INACTIVE_ERROR("Users account is inactive",400),
    EMPTY_USERNAME_ERROR("Username cant be empty",400),
    EMPTY_PASSWORD_ERROR("Password cant be empty",400),
    EMPTY_EMAIL_ERROR("Email cant be empty",400),
    USERNAME_ALREADY_EXISTS("Username already exists in database",409),
    EMAIL_ALREADY_EXISTS("Email already exists in database",409),
    USERNAME_AND_EMAIL_ALREADY_EXISTS("Username and email already exists in database",409),
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

