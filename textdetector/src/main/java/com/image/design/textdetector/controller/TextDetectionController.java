package com.image.design.textdetector.controller;

import com.image.design.textdetector.model.FileExtension;
import com.image.design.textdetector.model.TextAreaDetector;
import com.image.design.textdetector.model.TextDetector;
import com.image.design.textdetector.service.FileHandlerService;
import com.image.design.textdetector.service.FilePathService;
import com.image.design.textdetector.service.FileStoreService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ocr")
@AllArgsConstructor
public class TextDetectionController {

    private final TextDetector textDetector;
    private final TextAreaDetector textAreaDetector;
    private final FileHandlerService fileHandlerService;
    private final FileStoreService fileStoreService;
    private final FilePathService filePathService;

    @PostMapping("/detect")
    public String detectText(@RequestParam("files") MultipartFile[] multipartFiles){
        final List<Path> paths = new ArrayList<>();

        for(final MultipartFile multipartFile : multipartFiles) {
            final FileExtension fileExtension = this.fileHandlerService.getFileExtension(multipartFile);

            if(fileExtension == FileExtension.NOT_ALLOWED) {
                continue;
            }

            final byte[] inputFileBytes = this.fileHandlerService.getImageWithProperOrientation(multipartFile, fileExtension);
            final byte[] detectedCodeImage = this.textAreaDetector.detect(inputFileBytes);
            final String detectedCode = this.textDetector.detect(detectedCodeImage);

            paths.add(this.fileStoreService.storeFile(multipartFile, detectedCode, fileExtension));
        }

        return paths.stream()
                .map(path -> {
                    final String serverPath = path.toString();
                    return this.filePathService.getFileContextPath(serverPath.substring(serverPath.lastIndexOf("\\") + 1));
                })
                .collect(Collectors.joining("\n"));
    }
}
