package com.image.design.textdetector.exception.handler;

import com.image.design.textdetector.exception.BaseException;
import com.image.design.textdetector.exception.Error;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.logging.Logger;

@ControllerAdvice
public class TextDetectorExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(TextDetectorExceptionHandler.class.getName());

    @ResponseBody
    @ExceptionHandler(value = { BaseException.class })
    public ResponseEntity<Error> handle(final BaseException ex) {
        LOGGER.warning(String.format("App thrown exception: %s", ex.toString()));
        return ResponseEntity.badRequest().body(new Error(ex.getMessage()));
    }
}
