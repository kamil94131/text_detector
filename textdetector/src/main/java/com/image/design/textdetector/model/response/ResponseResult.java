package com.image.design.textdetector.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseResult {

    @JsonProperty
    private String message;

    @JsonProperty
    private ResponseType type;
}
