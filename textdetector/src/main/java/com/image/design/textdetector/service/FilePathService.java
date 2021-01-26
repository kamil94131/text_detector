package com.image.design.textdetector.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
public class FilePathService {

    public String getFullPathUrl(final String fileName) {
        final HttpServletRequest request = getRequest();

        if(Objects.isNull(request)) {
            return "";
        }

        final String context = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return String.format("%s/store/images/%s", context, fileName);
    }

    public String getDirectoryPathUrl() {
        final HttpServletRequest request = getRequest();

        if(Objects.isNull(request)) {
            return "";
        }
        final String context = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return String.format("%s/store/images", context);
    }

    private HttpServletRequest getRequest() {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if(Objects.isNull(attributes)) {
            return null;
        }

        return ((ServletRequestAttributes) attributes).getRequest();
    }
}
