package com.image.design.textdetector.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties(prefix = "filestorage")
public class FileStorageProperty {

    private String uploadDirectory;

    public String getUploadDirectory() {
        return uploadDirectory;
    }

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    public Path getPath() {
        return Paths.get(this.uploadDirectory).toAbsolutePath().normalize();
    }
}
