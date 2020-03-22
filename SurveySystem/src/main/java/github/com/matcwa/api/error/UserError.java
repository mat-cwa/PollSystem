package github.com.matcwa.api.error;

import github.com.matcwa.infrastructure.ResponseError;

public enum UserError implements ResponseError {
    USER_NOT_FOUND_ERROR("User not exist", 404);
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

