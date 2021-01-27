package com.image.design.textdetector.model.file;

import java.util.Objects;
import java.util.stream.Collectors;
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
        if(Objects.isNull(extension) || extension.isBlank()) {
            return FileExtension.NOT_ALLOWED;
        }

        return Stream.of(FileExtension.values())
                .filter(allowedExtension -> allowedExtension.name().equalsIgnoreCase(extension))
                .findFirst()
                .orElse(FileExtension.NOT_ALLOWED);
    }

    public static String getFormattedExtensions() {
        return Stream.of(FileExtension.values())
                .filter(fileExtension -> fileExtension != FileExtension.NOT_ALLOWED)
                .map(fileExtension -> fileExtension.name().toLowerCase())
                .collect(Collectors.joining(","));
    }
}
