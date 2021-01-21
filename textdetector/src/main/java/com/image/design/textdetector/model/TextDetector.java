package com.image.design.textdetector.model;

import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.exception.BaseException;
import com.image.design.textdetector.service.FileHandlerService;
import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.Optional;

@Component
@AllArgsConstructor
public class TextDetector {

    private final Tesseract tesseract;
    private final MessageResource messageResource;
    private final FileHandlerService conversionService;

    public String detect(byte[] data) {
        if(Optional.ofNullable(data).isEmpty() || data.length == 0) {
            throw new BaseException(this.messageResource.get("imagedesign.error.textarea.notfound"));
        }

        try {
            final BufferedImage image = this.conversionService.convertBytesToImage(data);
            return tesseract.doOCR(image);
        } catch(Exception e) {
            throw new BaseException(this.messageResource.get("imagedesign.error.ocr"));
        }
    }
}
