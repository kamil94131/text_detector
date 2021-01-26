package com.image.design.textdetector.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Error {

    @JsonProperty
    private String message;
}
