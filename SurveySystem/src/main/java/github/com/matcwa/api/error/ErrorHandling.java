package github.com.matcwa.api.error;

import github.com.matcwa.infrastructure.error.ResponseError;

public class ErrorHandling<T, U extends ResponseError> {
    private T dto;
    private U error;

    public T getDto() {
        return dto;
    }

    public void setDto(T dto) {
        this.dto = dto;
    }

    public U getError() {
        return error;
    }

    public void setError(U error) {
        this.error = error;
    }
}
