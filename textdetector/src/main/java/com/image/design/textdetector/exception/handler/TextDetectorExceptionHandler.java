package com.image.design.textdetector.exception.handler;

import com.image.design.textdetector.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.logging.Logger;

@ControllerAdvice
public class TextDetectorExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(TextDetectorExceptionHandler.class.getName());

    @ExceptionHandler(value = { BaseException.class })
    public ResponseEntity<String> handle(final BaseException ex) {
        LOGGER.warning(String.format("App thrown exception: %s", ex.toString()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
