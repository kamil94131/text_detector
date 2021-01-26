package com.image.design.textdetector.model.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ProtocolWrapper {

    @JsonProperty
    private final List<FileProcessingProtocol> protocol = new ArrayList<>();

    public ProtocolWrapper() {}

    public void add(final FileProcessingProtocol fileProcessingProtocol) {
        if(Objects.isNull(fileProcessingProtocol)) {
            return;
        }

        this.protocol.add(fileProcessingProtocol);
    }
}
