package com.image.design.textdetector.configuration;

import com.image.design.textdetector.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class TextDetectorExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { BaseException.class })
    public ResponseEntity<String> handle(final BaseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
