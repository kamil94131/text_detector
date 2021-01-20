package com.image.design.textdetector.service;

import com.image.design.textdetector.configuration.FileStorageProperty;
import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.exception.BaseException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.LongStream;

@Service
public class FileStoreService {

    private final MessageResource messageResource;
    private final FileStorageProperty fileStorageProperty;

    public FileStoreService(final FileStorageProperty fileStorageProperty, final MessageResource messageResource) {
        this.messageResource = messageResource;
        this.fileStorageProperty = fileStorageProperty;
        try {
            Files.createDirectories(this.fileStorageProperty.getPath());
        } catch (IOException e) {
            // TODO
        }
    }

    public void storeFile(final MultipartFile multipartFile, final String fileName) {
        try {
            final String name = fileName.replace(' ', '_').trim();
            final String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            final Path path = this.generatePath(name, extension);

            Files.copy(multipartFile.getInputStream(), path);
        } catch (IOException e) {
            throw new BaseException(this.messageResource.get("imagedesign.error.imagestore.save"));
        }
    }

    private Path generatePath(final String fileName, final String extension) {
        final Path path = this.getPath(fileName, extension);

        if(!this.fileExistsInStore(path)) {
            return path;
        }

        return LongStream.iterate(1, index -> index += 1).limit(Long.MAX_VALUE)
                .mapToObj(index -> this.generateNextFileName(fileName, extension, index))
                .filter(filePath -> !this.fileExistsInStore(filePath))
                .findAny()
                .orElseThrow(() -> new BaseException(this.messageResource.get("imagedesign.error.file.uniquename")));
    }

    private Path generateNextFileName(final String fileName, final String extension, final long index) {
        return this.getPath(String.format("%s-%d", fileName, index), extension);
    }

    public Resource getFile(final String fileName) {
        final Path path = this.getPath(fileName);

        try {
            final Resource resource = new UrlResource(path.toUri());

            if(!resource.exists()) {
                throw new BaseException(this.messageResource.get("imagedesign.error.imagestore.notfound"));
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new BaseException(this.messageResource.get("imagedesign.error.imagestore.notfound"));
        }
    }

    private boolean fileExistsInStore(final Path path) {
        if(Objects.isNull(path)) {
            return false;
        }

        try {
            final Resource resource = new UrlResource(path.toUri());
            return resource.exists();
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private Path getPath(final String fileName) {
        return Paths.get(String.format("%s\\%s", this.fileStorageProperty.getUploadDirectory(), fileName));
    }

    private Path getPath(final String fileName, final String extension) {
        final String pathWithFileName = this.getPath(fileName).toString();
        return Paths.get(String.format("%s.%s", pathWithFileName, extension));
    }
}
