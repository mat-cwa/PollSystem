package github.com.matcwa.api.error;

import github.com.matcwa.infrastructure.ResponseError;

public enum  TokenError implements ResponseError {
    TOKEN_NOT_FOUND_ERROR("Token not exist", 404),
    TOKEN_IS_INACTIVE("Token is inactive",400);

    private String message;
    private int httpCode;

    TokenError(String message, int httpCode) {
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
