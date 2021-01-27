package com.image.design.textdetector.model.detector;

import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.service.FileHandlerService;
import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class TextDetector {

    private static final Logger LOGGER = Logger.getLogger(TextDetector.class.getName());

    private final Tesseract tesseract;
    private final MessageResource messageResource;
    private final FileHandlerService conversionService;

    public String detect(byte[] data) {
        try {
            final BufferedImage image = this.conversionService.convertBytesToImage(data);
            return tesseract.doOCR(image).replace(' ', '_').trim();
        } catch(Exception e) {
            LOGGER.warning(this.messageResource.get("system.error.ocr", e.toString()));
            return "";
        }
    }
}
