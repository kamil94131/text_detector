package com.image.design.textdetector.controller;

import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.model.response.ProtocolResponseResult;
import com.image.design.textdetector.model.response.ResponseType;
import com.image.design.textdetector.service.DetectionProcessService;
import com.image.design.textdetector.service.ProtocolService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("ocr")
@AllArgsConstructor
public class TextDetectionController {

    private final DetectionProcessService detectionProcessService;
    private final ProtocolService protocolService;
    private final MessageResource messageResource;

    @PostMapping(value = "detect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProtocolResponseResult> detectText(@RequestParam("files") final MultipartFile[] multipartFiles){
        for(final MultipartFile multipartFile : multipartFiles) {
            this.detectionProcessService.process(multipartFile);
        }

        final ProtocolResponseResult result = this.protocolService.getProtocol().getProtocolResponseResult();
        final boolean containsErrors = result.getProtocol().stream().noneMatch(protocol -> Objects.isNull(protocol.getDetail()));

        if(!containsErrors) {
            result.setType(ResponseType.SUCCESS);
            result.setMessage(this.messageResource.get("imagedesign.success.detection.process"));
        } else {
            result.setType(ResponseType.WARNING);
            result.setMessage(this.messageResource.get("imagedesign.warning.detection.process"));
        }
        return ResponseEntity.ok(this.protocolService.getProtocol().getProtocolResponseResult());
    }
}
