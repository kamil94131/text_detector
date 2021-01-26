package com.image.design.textdetector.controller;

import com.image.design.textdetector.model.protocol.ProtocolWrapper;
import com.image.design.textdetector.service.DetectionProcessService;
import com.image.design.textdetector.service.ProtocolService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("ocr")
@AllArgsConstructor
public class TextDetectionController {

    private final DetectionProcessService detectionProcessService;
    private final ProtocolService protocolService;

    @PostMapping("detect")
    public ResponseEntity<ProtocolWrapper> detectText(@RequestParam("files") final MultipartFile[] multipartFiles){
        for(final MultipartFile multipartFile : multipartFiles) {
            this.detectionProcessService.process(multipartFile);
        }

        return ResponseEntity.ok(this.protocolService.getProtocol().getProtocolWrapper());
    }
}
