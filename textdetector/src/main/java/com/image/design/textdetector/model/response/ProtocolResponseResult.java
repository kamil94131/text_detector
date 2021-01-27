package com.image.design.textdetector.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.image.design.textdetector.model.protocol.FileProcessingProtocol;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ProtocolResponseResult {

    @JsonProperty
    private String message;

    @JsonProperty
    private ResponseType type;

    @JsonProperty
    private List<FileProcessingProtocol> protocol;

    public ProtocolResponseResult() {}

    public void add(final FileProcessingProtocol fileProcessingProtocol) {
        if(Objects.isNull(this.protocol)) {
            this.protocol = new ArrayList<>();
        }

        this.protocol.add(fileProcessingProtocol);
    }
}
