package com.image.design.textdetector.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class ResponseResult implements Serializable {

    @JsonProperty
    private String message;

    @JsonProperty
    private ResponseType type;
}
