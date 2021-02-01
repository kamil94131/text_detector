package com.image.design.textdetector.controller;

import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.model.response.FileUrlResponseResult;
import com.image.design.textdetector.model.response.ResponseResult;
import com.image.design.textdetector.model.response.ResponseType;
import com.image.design.textdetector.service.FileStoreService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("store/images")
public class FileStoreController {

    private final FileStoreService fileStoreService;
    private final MessageResource messageResource;

    public FileStoreController(FileStoreService fileStoreService, MessageResource messageResource) {
        this.fileStoreService = fileStoreService;
        this.messageResource = messageResource;
    }

    @GetMapping(value = "{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadImage(@PathVariable String fileName) {
        final Resource resource = this.fileStoreService.getStoredFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileUrlResponseResult> getImagesUrls() {
        final List<String> urls = this.fileStoreService.getApiUrlsToStoredFiles();

        final FileUrlResponseResult result = new FileUrlResponseResult();

        if(!urls.isEmpty()) {
            result.setType(ResponseType.SUCCESS);
            result.addUrls(urls);
            result.setMessage(this.messageResource.get("imagedesign.success.image.found.url"));
        } else {
            result.setType(ResponseType.WARNING);
            result.setMessage(this.messageResource.get("imagedesign.warning.image.notfound.urls"));
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "{fileName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseResult> deleteImage(@PathVariable String fileName) {
        this.fileStoreService.deleteStoredFile(fileName);
        final String message = this.messageResource.get("imagedesign.success.file.deleted");
        return ResponseEntity.ok(new ResponseResult(message, ResponseType.SUCCESS));
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseResult> deleteImages() {
        this.fileStoreService.deleteStoredFiles();
        final String message = this.messageResource.get("imagedesign.success.files.deleted");
        return ResponseEntity.ok(new ResponseResult(message, ResponseType.SUCCESS));
    }
}
