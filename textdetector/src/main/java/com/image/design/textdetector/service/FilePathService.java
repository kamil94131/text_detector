package com.image.design.textdetector.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FilePathService {

    @Value("${server.port}")
    private String serverPort;

    @Value(("${server.address}"))
    private String serverIP;

    public String getFileContextPath(final String fileName) {
        return String.format("%s:%s/download/image/%s", this.serverIP, this.serverPort, fileName);
    }
}
