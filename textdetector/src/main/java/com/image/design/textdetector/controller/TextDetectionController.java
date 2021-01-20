package com.image.design.textdetector.controller;

import com.image.design.textdetector.model.TextAreaDetector;
import com.image.design.textdetector.model.TextDetector;
import com.image.design.textdetector.service.InputConversionService;
import lombok.AllArgsConstructor;
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

    @RequestMapping(value = "/detect")
    public String detectText(@RequestParam("files") MultipartFile[] files) {
        final List<String> codes = new ArrayList<>();

        for(final MultipartFile file : files) {
            final byte[] inputFileBytes = this.inputConversionService.getBytes(file);
            final byte[] detectedNumber = this.textAreaDetector.detect(inputFileBytes);
            final String selectedFileCode = this.textDetector.detect(detectedNumber);
            codes.add(selectedFileCode);
        }

        return String.join(",", codes);
    }
}
