package com.image.design.textdetector.service;

import com.image.design.textdetector.configuration.FileStorageProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
public class FilePathService {

    private FileStorageProperty fileStorageProperty;

    public FilePathService(FileStorageProperty fileStorageProperty) {
        this.fileStorageProperty = fileStorageProperty;
    }

    public String getFullPathUrl(final String fileName) {
        final HttpServletRequest request = getRequest();

        if(Objects.isNull(request)) {
            return "";
        }

        final String context = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return String.format("%s/%s/%s", context, this.fileStorageProperty.getUploadDirectory(), fileName);
    }

    public String getDirectoryPathUrl() {
        final HttpServletRequest request = getRequest();

        if(Objects.isNull(request)) {
            return "";
        }
        final String context = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return String.format("%s/%s", context, this.fileStorageProperty.getUploadDirectory());
    }

    private HttpServletRequest getRequest() {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

        if(Objects.isNull(attributes)) {
            return null;
        }

        return ((ServletRequestAttributes) attributes).getRequest();
    }
}
