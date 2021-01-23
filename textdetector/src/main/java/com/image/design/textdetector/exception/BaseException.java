package com.image.design.textdetector.exception;

public class BaseException extends RuntimeException {

    private String message;

    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseException{" +
                "message='" + message + '\'' +
                '}';
    }
}
