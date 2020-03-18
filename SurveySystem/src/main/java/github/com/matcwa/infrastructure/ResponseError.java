package github.com.matcwa.infrastructure;

public interface ResponseError {
    String getMessage();

    int getHttpCode();
}
