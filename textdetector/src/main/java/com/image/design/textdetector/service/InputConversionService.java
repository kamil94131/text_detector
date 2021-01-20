package com.image.design.textdetector.service;

import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.exception.BaseException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@AllArgsConstructor
public class InputConversionService {

    private final MessageResource messageResource;

    public byte[] getBytes(final MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new BaseException(this.messageResource.get("imagedesign.error.inputfile.read"));
        }
    }
}
