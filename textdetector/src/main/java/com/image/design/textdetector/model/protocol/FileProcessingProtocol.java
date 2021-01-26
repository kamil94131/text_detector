package com.image.design.textdetector.model.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class FileProcessingProtocol {

    @JsonProperty
    private String url;

    @JsonProperty
    private String fileName;

    @JsonProperty
    private String code;

    @JsonProperty
    private List<Detail> details = new ArrayList<>();


    public void add(final Detail detail) {
        if(Objects.isNull(detail)) {
            return;
        }

        this.details.add(detail);
    }
}
