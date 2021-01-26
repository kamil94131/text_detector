package com.image.design.textdetector.service;

import com.image.design.textdetector.model.protocol.FileProcessingProtocol;
import com.image.design.textdetector.model.protocol.Detail;
import com.image.design.textdetector.model.protocol.ProcessingProtocol;
import org.springframework.stereotype.Service;

@Service
public class ProtocolService {

    private final ProcessingProtocol processingProtocol;

    public ProtocolService(final ProcessingProtocol processingProtocol) {
        this.processingProtocol = processingProtocol;
    }

    public void beginGeneration() {
        final FileProcessingProtocol protocol = new FileProcessingProtocol();
        this.processingProtocol.getProtocolWrapper().add(protocol);
    }

    public void addMessage(final Detail detail) {
        final FileProcessingProtocol fileProcessingProtocol = this.getCurrentFileProcessingProtocol();
        fileProcessingProtocol.add(detail);
    }

    public ProcessingProtocol getProtocol() {
        return this.processingProtocol;
    }

    private FileProcessingProtocol getCurrentFileProcessingProtocol() {
        return this.processingProtocol.getProtocolWrapper().getProtocol().stream()
                .reduce((previous, next) -> next)
                .orElse(new FileProcessingProtocol());
    }

    public void finishGeneration(final String url, final String code, final String fileName) {
        final FileProcessingProtocol fileProcessingProtocol = this.getCurrentFileProcessingProtocol();
        fileProcessingProtocol.setFileName(fileName);
        fileProcessingProtocol.setUrl(url);
        fileProcessingProtocol.setCode(code);
    }

    public void finishGeneration(final String fileName) {
        this.finishGeneration("", "", fileName);
    }
}
