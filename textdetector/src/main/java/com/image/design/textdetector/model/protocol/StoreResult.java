package com.image.design.textdetector.model.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
@AllArgsConstructor
public class StoreResult {

    private String message;
    private Path path;
}
