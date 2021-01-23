package com.image.design.textdetector.service;

import com.image.design.textdetector.configuration.FileStorageProperty;
import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.exception.BaseException;
import com.image.design.textdetector.model.FileExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.LongStream;

@Service
public class FileStoreService {

    private static final Logger LOGGER = Logger.getLogger(FileStoreService.class.getName());
    private final MessageResource messageResource;
    private final FileStorageProperty fileStorageProperty;

    public FileStoreService(final FileStorageProperty fileStorageProperty, final MessageResource messageResource) {
        this.messageResource = messageResource;
        this.fileStorageProperty = fileStorageProperty;

        try {
            Files.createDirectories(this.fileStorageProperty.getPath());
        } catch (IOException e) {
            LOGGER.warning(String.format("Couldn't create directory for file storage, ex: %s", e.toString()));
        }
    }

    public Path storeFile(final MultipartFile multipartFile, final String fileName, final FileExtension fileExtension) {
        try {
            final String name = fileName.replace(' ', '_').trim();
            final Path path = this.generatePath(name, fileExtension.name().toLowerCase());

            Files.copy(multipartFile.getInputStream(), path);

            return path;
        } catch (IOException e) {
            throw new BaseException(this.messageResource.get("imagedesign.error.imagestore.save"));
        }
    }

    private Path generatePath(final String fileName, final String extension) {
        final Path path = this.getPath(fileName, extension);

        if(Objects.isNull(path) || !this.fileExists(path)) {
            return path;
        }

        return LongStream.iterate(1, index -> index += 1).limit(Long.MAX_VALUE)
                .mapToObj(index -> this.generateNextFileName(fileName, extension, index))
                .filter(filePath -> !this.fileExists(filePath))
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

    private boolean fileExists(final Path path) {
        if(Objects.isNull(path)) {
            return false;
        }

        try {
            final Resource resource = new UrlResource(path.toUri());
            return resource.exists();
        } catch (MalformedURLException e) {
            LOGGER.warning(String.format("Couldn't get file as resource, ex: %s", e.toString()));
            return false;
        }
    }

    private Path getPath(final String fileName) {
        try {
            return Paths.get(String.format("%s\\%s", this.fileStorageProperty.getUploadDirectory(), fileName));
        } catch(InvalidPathException e) {
            LOGGER.warning(String.format("Couldn't get path for file name: %s, ex: %s", fileName, e.toString()));
            return null;
        }
    }

    private Path getPath(final String fileName, final String extension) {
        final Optional<Path> pathWithFileName = Optional.ofNullable(this.getPath(fileName));

        if(pathWithFileName.isEmpty()) {
            return null;
        }

        try {
            return Paths.get(String.format("%s.%s", pathWithFileName.get().toString(), extension));
        } catch(InvalidPathException e) {
            LOGGER.warning(String.format("Couldn't get path for file name: %s and extension: %s, ex: %s", fileName, extension, e.toString()));
            return null;
        }
    }
}
