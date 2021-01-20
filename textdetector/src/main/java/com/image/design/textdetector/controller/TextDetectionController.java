package com.image.design.textdetector.controller;

import com.image.design.textdetector.model.TextAreaDetector;
import com.image.design.textdetector.model.TextDetector;
import com.image.design.textdetector.service.FileStoreService;
import com.image.design.textdetector.service.InputConversionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("ocr")
@AllArgsConstructor
public class TextDetectionController {

    private final TextDetector textDetector;
    private final TextAreaDetector textAreaDetector;
    private final InputConversionService inputConversionService;
    private final FileStoreService fileStoreService;

    @PostMapping("/detect")
    public String detectText(@RequestParam("files") MultipartFile[] multipartFiles) {
        final List<String> codes = new ArrayList<>();

        for(final MultipartFile multipartFile : multipartFiles) {
            final byte[] inputFileBytes = this.inputConversionService.getMultipartFileBytes(multipartFile);
            final byte[] detectedCodeImage = this.textAreaDetector.detect(inputFileBytes);
            final String detectedCode = this.textDetector.detect(detectedCodeImage);
            codes.add(detectedCode);

            this.fileStoreService.storeFile(multipartFile, detectedCode);
        }

        return String.join(",", codes);
    }
}
