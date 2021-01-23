package com.image.design.textdetector.model;

import java.util.stream.Stream;

public enum FileExtension {
    NOT_ALLOWED,
    TIFF,
    JPG,
    JPEG,
    GIF,
    PNG,
    BMP;

    public static FileExtension getExtension(final String extension) {
        return Stream.of(FileExtension.values())
                .filter(allowedExtension -> allowedExtension.name().equalsIgnoreCase(extension))
                .findFirst()
                .orElse(FileExtension.NOT_ALLOWED);
    }
}
