package com.image.design.textdetector.constroller;

import com.image.design.textdetector.model.TextAreaDetector;
import com.image.design.textdetector.model.TextDetector;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ocr")
@AllArgsConstructor
public class TextDetectionController {

    private final TextDetector textDetector;
    private final TextAreaDetector textAreaDetector;

    @RequestMapping(value = "/detect")
//    @RequestMapping(value = "/detect", consumes = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    public String detectText(@RequestParam("file") MultipartFile file) throws IOException {
        final List<byte[]> detectedNumbers = this.textAreaDetector.detect(file.getBytes());
        final String result = detectedNumbers.stream()
                .map(detectedNumber -> this.textDetector.detect(detectedNumber))
                .collect(Collectors.joining(","));
        return result;
    }
}
