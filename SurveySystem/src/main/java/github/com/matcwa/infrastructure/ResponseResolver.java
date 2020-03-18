package github.com.matcwa.infrastructure;

import github.com.matcwa.api.error.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ResponseResolver {


    public static ResponseEntity resolve(Optional<?> data) {
        return data.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public static ResponseEntity resolve(ErrorHandling<?, ? extends ResponseError> data) {
        if (data.getError() != null) {
            ErrorJS errorforjs = new ErrorJS(data.getError().getMessage());
            return new ResponseEntity<>(errorforjs, HttpStatus.valueOf(data.getError().getHttpCode()));
        }
        return ResponseEntity.ok(data.getDto());
    }

}
