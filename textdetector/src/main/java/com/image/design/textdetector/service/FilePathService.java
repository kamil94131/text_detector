package com.image.design.textdetector.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class FilePathService {

    public String getFileContextPath(final HttpServletRequest request, final String fileName) {
        final String context = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return String.format("%s/download/image/%s", context, fileName);
    }
}
