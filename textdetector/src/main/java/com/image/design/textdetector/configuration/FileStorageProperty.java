package com.image.design.textdetector.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@ConfigurationProperties(prefix = "filestorage")
public class FileStorageProperty {

    private static final Logger LOGGER = Logger.getLogger(FileStorageProperty.class.getName());
    private String uploadDirectory;

    public String getUploadDirectory() {
        return uploadDirectory;
    }

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    public Path getPath() {
        try {
            return Paths.get(this.uploadDirectory).toAbsolutePath().normalize();
        } catch(InvalidPathException e) {
            LOGGER.warning(String.format("Couldn't get file store path, ex: %s", e.toString()));
            return null;
        }
    }
}
