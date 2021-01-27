package com.image.design.textdetector.service;

import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.model.detector.TextAreaDetector;
import com.image.design.textdetector.model.detector.TextDetector;
import com.image.design.textdetector.model.file.FileExtension;
import com.image.design.textdetector.model.protocol.StoreResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@AllArgsConstructor
public class DetectionProcessService {

    private final TextDetector textDetector;
    private final TextAreaDetector textAreaDetector;
    private final ProtocolService protocolService;
    private final FileHandlerService fileHandlerService;
    private final MessageResource messageResource;
    private final FileStoreService fileStoreService;
    private final FilePathService filePathService;

    public void process(final MultipartFile multipartFile) {
        this.protocolService.beginGeneration();

        final FileExtension fileExtension = this.fileHandlerService.getFileExtension(multipartFile);

        if(fileExtension == FileExtension.NOT_ALLOWED) {
            this.appendExtensionNotAllowedToProtocol();
            this.protocolService.finishGeneration(multipartFile.getOriginalFilename());
            return;
        }

        final String detectedCode = this.detect(multipartFile, fileExtension);

        if(detectedCode.isBlank()) {
            return;
        }

        this.store(multipartFile, detectedCode, fileExtension);
    }

    private String detect(final MultipartFile multipartFile, final FileExtension fileExtension) {
        final byte[] imageWithProperOrientation = this.fileHandlerService.getImageWithProperOrientation(multipartFile, fileExtension);

        if(imageWithProperOrientation.length == 0) {
            this.appendResourceToProtocol("imagedesign.error.file.unknown.problem");
            this.protocolService.finishGeneration(multipartFile.getOriginalFilename());
            return "";
        }

        final byte[] detectedCodeAreaImage = this.textAreaDetector.detect(imageWithProperOrientation, fileExtension);

        if(detectedCodeAreaImage.length == 0) {
            this.appendResourceToProtocol("imagedesign.error.file.unknown.problem");
            this.protocolService.finishGeneration(multipartFile.getOriginalFilename());
            return "";
        }

        if(imageWithProperOrientation == detectedCodeAreaImage) {
            this.appendResourceToProtocol("imagedesign.error.code.are.notfound");
            this.protocolService.finishGeneration(multipartFile.getOriginalFilename());
            return "";
        }

        final String detectedCode = this.textDetector.detect(detectedCodeAreaImage);

        if(detectedCode.isBlank()) {
            this.appendResourceToProtocol("imagedesign.error.ocr");
            this.protocolService.finishGeneration(multipartFile.getOriginalFilename());
            return "";
        }

        return detectedCode;
    }

    private void store(final MultipartFile multipartFile, final String detectedCode, final FileExtension fileExtension) {
        final StoreResult storeResult = this.fileStoreService.storeFile(multipartFile, detectedCode, fileExtension);

        if(Objects.isNull(storeResult.getPath())) {
            this.appendToProtocol(storeResult.getMessage());
            this.protocolService.finishGeneration(multipartFile.getOriginalFilename());
            return;
        }

        final String serverPath = storeResult.getPath().toString();
        final String fileName = serverPath.substring(serverPath.lastIndexOf("\\") + 1);
        final String resourcePath = this.filePathService.getFullPathUrl(fileName);

        if(Objects.isNull(resourcePath)) {
            this.appendResourceToProtocol("imagedesign.error.imagestore.save");
            this.protocolService.finishGeneration(multipartFile.getOriginalFilename());
            return;
        }
        this.protocolService.finishGeneration(resourcePath,detectedCode, multipartFile.getOriginalFilename());
    }

    private void appendExtensionNotAllowedToProtocol() {
        final String availableExtensions = FileExtension.getFormattedExtensions();
        final String detail = this.messageResource.get("imagedesign.error.file.extension", availableExtensions);
        this.protocolService.setMessage(detail);
    }

    private void appendResourceToProtocol(final String resource) {
        final String detail = this.messageResource.get(resource);
        this.protocolService.setMessage(detail);
    }

    private void appendToProtocol(final String detail) {
        this.protocolService.setMessage(detail);
    }
}
