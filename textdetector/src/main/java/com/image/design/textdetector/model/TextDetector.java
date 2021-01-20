package com.image.design.textdetector.model;

import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.exception.BaseException;
import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Component
@AllArgsConstructor
public class TextDetector {

    private final Tesseract tesseract;
    private final MessageResource messageResource;

    public String detect(byte[] data) {
        if(Optional.ofNullable(data).isEmpty() || data.length == 0) {
            throw new BaseException(this.messageResource.get("imagedesign.error.textarea.notfound"));
        }

        try {
            final BufferedImage image = getImageFromBytes(data);
            return tesseract.doOCR(image);
        } catch(Exception e) {
            throw new BaseException(this.messageResource.get("imagedesign.error.ocr"));
        }
    }

    private BufferedImage getImageFromBytes(byte[] data) {
        try {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new BaseException(this.messageResource.get("imagedesign.error.image.conversion"));
        }
    }
}
