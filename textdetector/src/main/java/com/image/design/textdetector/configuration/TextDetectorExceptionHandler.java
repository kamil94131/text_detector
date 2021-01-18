package com.image.design.textdetector.configuration;

import com.image.design.textdetector.exception.ImageConvertionException;
import com.image.design.textdetector.exception.TesseractDetectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class TextDetectorExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { TesseractDetectionException.class, ImageConvertionException.class })
    public ResponseEntity<String> handle(final TesseractDetectionException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
