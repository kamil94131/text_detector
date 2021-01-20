package com.image.design.textdetector.controller;

import com.image.design.textdetector.service.FileStoreService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("download")
@AllArgsConstructor
public class FileStoreController {

    private final FileStoreService fileStoreService;

    @GetMapping("image/{fileName}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String fileName) {
        final Resource resource = this.fileStoreService.getFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
    }
}
