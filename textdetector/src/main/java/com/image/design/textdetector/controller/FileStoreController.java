package com.image.design.textdetector.controller;

import com.image.design.textdetector.model.link.FileUrl;
import com.image.design.textdetector.service.FileStoreService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("store/images")
@AllArgsConstructor
public class FileStoreController {

    private static final Logger LOGGER = Logger.getLogger(FileStoreController.class.getName());
    private final FileStoreService fileStoreService;

    @GetMapping("{fileName}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String fileName) {
        final Resource resource = this.fileStoreService.getStoredFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileUrl> getImagesUrls() {
        final FileUrl fileUrls = this.fileStoreService.getUrlsToStoredFiles();
        return ResponseEntity.ok().body(fileUrls);
    }

    @DeleteMapping("{fileName}")
    public ResponseEntity<String> deleteImage(@PathVariable String fileName) {
        this.fileStoreService.deleteStoredFile(fileName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteImages() {
        this.fileStoreService.deleteStoredFiles();
        return ResponseEntity.ok().build();
    }
}
