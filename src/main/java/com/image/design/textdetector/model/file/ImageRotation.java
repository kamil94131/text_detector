package com.image.design.textdetector.model.file;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum ImageRotation {
    DEGREE_NOT_DETECTED(-1, 0),
    DEGREE_0(1, 0),
    DEGREE_90(6, 90),
    DEGREE_180(3, 180),
    DEGREE_270(8, 270);

    private final int rotation;
    private final int radians;

    public static ImageRotation getByRotation(int rotation) {
        return Stream.of(ImageRotation.values())
                .filter(imageRotation -> imageRotation.rotation == rotation)
                .findAny()
                .orElse(ImageRotation.DEGREE_NOT_DETECTED);
    }

    ImageRotation(int rotation, int radians) {
        this.rotation = rotation;
        this.radians = radians;
    }
}
