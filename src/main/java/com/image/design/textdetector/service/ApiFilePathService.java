package com.image.design.textdetector.service;

import com.image.design.textdetector.configuration.FileStorageProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class ApiFilePathService {

    private final FileStorageProperty fileStorageProperty;

    public ApiFilePathService(FileStorageProperty fileStorageProperty) {
        this.fileStorageProperty = fileStorageProperty;
    }

    public String getFullPathUrl(final String fileName) {
        final String context = this.getContextPath();
        return String.format("%s/%s/%s", context, this.fileStorageProperty.getContextWithUploadDirectory(), fileName);
    }

    public String getDirectoryPathUrl() {
        final String context = this.getContextPath();
        return String.format("%s/%s", context, this.fileStorageProperty.getContextWithUploadDirectory());
    }

    private String getContextPath() {
        return getUriPath().replace(getOriginalPath(), "");
    }

    private String getUriPath() {
        return ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
    }

    private String getOriginalPath() {
        return ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
    }
}
