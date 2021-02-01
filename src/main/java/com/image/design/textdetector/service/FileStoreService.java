package com.image.design.textdetector.service;

import com.image.design.textdetector.configuration.FileStorageProperty;
import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.exception.BaseException;
import com.image.design.textdetector.model.file.FileExtension;
import com.image.design.textdetector.model.protocol.StoreResult;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class FileStoreService {

    private static final Logger LOGGER = Logger.getLogger(FileStoreService.class.getName());

    @Value("${detectedcode.prefix}")
    private String detectedCodePrefix;

    private final MessageResource messageResource;
    private final FileStorageProperty fileStorageProperty;
    private final ApiFilePathService apiFilePathService;

    public FileStoreService(final FileStorageProperty fileStorageProperty, final MessageResource messageResource, final ApiFilePathService apiFilePathService) {
        this.messageResource = messageResource;
        this.fileStorageProperty = fileStorageProperty;
        this.apiFilePathService = apiFilePathService;

        try {
            Files.createDirectories(this.fileStorageProperty.getPath());
        } catch (IOException e) {
            LOGGER.warning(this.messageResource.get("system.error.create.directory", e.toString()));
        }
    }

    public StoreResult storeFile(final MultipartFile multipartFile, final String fileName, final FileExtension fileExtension) {
        try {
            final Pattern pattern = Pattern.compile(String.format("^%s[0-9]{5}$", this.detectedCodePrefix));
            final Matcher matcher = pattern.matcher(fileName);

            if(!matcher.matches()) {
                return new StoreResult(this.messageResource.get("imagedesign.error.wrong.code.format", fileName), null);
            }

            final Path path = this.generateSystemPath(fileName, fileExtension.name().toLowerCase());

            if(Objects.isNull(path)) {
                return new StoreResult(this.messageResource.get("system.error.file.uniquename"), null);
            }

            Files.copy(multipartFile.getInputStream(), path);

            return new StoreResult(null, path);
        } catch (IOException e) {
            return new StoreResult(this.messageResource.get("imagedesign.error.cannot.get.file"), null);
        }
    }

    private Path generateSystemPath(final String fileName, final String extension) {
        final Path path = this.getSystemPath(fileName, extension);

        if(Objects.isNull(path) || !this.fileExists(path)) {
            return path;
        }

        return LongStream.iterate(1, index -> index += 1).limit(Long.MAX_VALUE)
                .mapToObj(index -> this.generateNextFileName(fileName, extension, index))
                .filter(filePath -> !this.fileExists(filePath))
                .findAny()
                .orElse(null);
    }

    private Path generateNextFileName(final String fileName, final String extension, final long index) {
        return this.getSystemPath(String.format("%s-%d", fileName, index), extension);
    }

    public List<String> getApiUrlsToStoredFiles() {
        final File filesDirectory = new File(this.fileStorageProperty.getUploadDirectory());
        if(filesDirectory.exists() && filesDirectory.isDirectory()) {
            final String[] filesNames = filesDirectory.list();

            if(Objects.isNull(filesNames) || filesNames.length == 0) {
                return new ArrayList<>();
            }

            final String directoryPathUrl = this.apiFilePathService.getDirectoryPathUrl();

            return Arrays.stream(filesNames)
                    .map(fileName -> String.format("%s/%s", directoryPathUrl, fileName))
                    .collect(Collectors.toList());
        } else {
            throw new BaseException(this.messageResource.get("imagedesign.error.directory.notfound"), HttpStatus.NOT_FOUND);
        }
    }

    public void deleteStoredFiles() {
        final File filesDirectory = new File(this.fileStorageProperty.getUploadDirectory());
        if(filesDirectory.exists() && filesDirectory.isDirectory()) {
            try {
                FileUtils.cleanDirectory(filesDirectory);
            } catch (IOException e) {
                throw new BaseException(this.messageResource.get("imagedesign.error.directory.cannot.clean"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new BaseException(this.messageResource.get("imagedesign.error.directory.notfound"), HttpStatus.NOT_FOUND);
        }
    }

    public void deleteStoredFile(final String fileName) {
        final Resource resource = getStoredFile(fileName);

        if(resource.exists() && resource.isFile()) {
            try {
                final File storedFile = resource.getFile();
                storedFile.delete();
            } catch (IOException e) {
                LOGGER.warning(this.messageResource.get("system.error.cannot.delete.file", e.toString()));
                throw new BaseException(this.messageResource.get("imagedesign.error.cannot.delete.file"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new BaseException(this.messageResource.get("imagedesign.error.cannot.delete.file"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Resource getStoredFile(final String fileName) {
        final Path path = this.getSystemPath(fileName);

        if(Objects.isNull(path)) {
            throw new BaseException(this.messageResource.get("imagedesign.error.imagestore.notfound"), HttpStatus.NOT_FOUND);
        }

        try {
            final Resource resource = new UrlResource(path.toUri());

            if(!resource.exists()) {
                throw new BaseException(this.messageResource.get("imagedesign.error.imagestore.notfound"), HttpStatus.NOT_FOUND);
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new BaseException(this.messageResource.get("imagedesign.error.imagestore.notfound"), HttpStatus.INTERNAL_SERVER_ERROR);
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

    private Path getSystemPath(final String fileName, final String extension) {
        final Optional<Path> pathWithFileName = Optional.ofNullable(this.getSystemPath(fileName));

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

    private Path getSystemPath(final String fileName) {
        try {
            return Paths.get(String.format("%s%s%s", this.fileStorageProperty.getUploadDirectory(), File.separator, fileName));
        } catch(InvalidPathException e) {
            LOGGER.warning(String.format("Couldn't get path for file name: %s, ex: %s", fileName, e.toString()));
            return null;
        }
    }
}
