package com.image.design.textdetector.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseException extends RuntimeException {

    private String message;
    private HttpStatus httpStatus;

    public BaseException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return "BaseException{" +
                "message='" + message + '\'' +
                '}';
    }
}
