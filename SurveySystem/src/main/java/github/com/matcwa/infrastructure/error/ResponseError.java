package github.com.matcwa.infrastructure.error;

public interface ResponseError {
    String getMessage();

    int getHttpCode();
}
