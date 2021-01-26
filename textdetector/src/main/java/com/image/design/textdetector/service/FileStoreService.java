package com.image.design.textdetector.service;

import com.image.design.textdetector.configuration.FileStorageProperty;
import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.exception.BaseException;
import com.image.design.textdetector.model.file.FileExtension;
import com.image.design.textdetector.model.link.FileUrl;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class FileStoreService {

    private static final String DETECTED_CODE_PATTERN = "^[a-zA-Z0-9_]+$";
    private static final Logger LOGGER = Logger.getLogger(FileStoreService.class.getName());
    private final MessageResource messageResource;
    private final FileStorageProperty fileStorageProperty;
    private final FilePathService filePathService;

    public FileStoreService(final FileStorageProperty fileStorageProperty, final MessageResource messageResource, final FilePathService filePathService) {
        this.messageResource = messageResource;
        this.fileStorageProperty = fileStorageProperty;
        this.filePathService = filePathService;

        try {
            Files.createDirectories(this.fileStorageProperty.getPath());
        } catch (IOException e) {
            LOGGER.warning(this.messageResource.getForSystem("system.error.create.directory", e.toString()));
        }
    }

    public Path storeFile(final MultipartFile multipartFile, final String fileName, final FileExtension fileExtension) {
        try {
            final Pattern pattern = Pattern.compile(DETECTED_CODE_PATTERN);
            final Matcher matcher = pattern.matcher(fileName);

            if(!matcher.matches()) {
                LOGGER.warning(this.messageResource.getForSystem("system.error.wrong.code.format", fileName, DETECTED_CODE_PATTERN));
                return null;
            }

            final Path path = this.generatePath(fileName, fileExtension.name().toLowerCase());

            if(Objects.isNull(path)) {
                LOGGER.warning(this.messageResource.getForSystem("system.error.file.uniquename"));
                return null;
            }

            Files.copy(multipartFile.getInputStream(), path);

            return path;
        } catch (IOException e) {
            LOGGER.warning(String.format("Couldn't get file as resource, ex: %s", e.toString()));
            return null;
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
                .orElse(null);
    }

    private Path generateNextFileName(final String fileName, final String extension, final long index) {
        return this.getPath(String.format("%s-%d", fileName, index), extension);
    }

    public FileUrl getUrlsToStoredFiles() {
        final File filesDirectory = new File(this.fileStorageProperty.getUploadDirectory());
        if(filesDirectory.exists() && filesDirectory.isDirectory()) {
            final String filesNames[] = filesDirectory.list();

            if(Objects.isNull(filesNames) || filesNames.length == 0) {
                return new FileUrl();
            }

            final String directoryPathUrl = this.filePathService.getDirectoryPathUrl();
            final FileUrl fileUrl = new FileUrl();
            Arrays.stream(filesNames).forEach(fileName -> fileUrl.addUrl(String.format("%s/%s", directoryPathUrl, fileName)));
            return fileUrl;
        } else {
            throw new BaseException(this.messageResource.get("imagedesign.error.directory.notfound"));
        }
    }

    public void deleteStoredFiles() {
        final File filesDirectory = new File(this.fileStorageProperty.getUploadDirectory());
        if(filesDirectory.exists() && filesDirectory.isDirectory()) {
            try {
                FileUtils.cleanDirectory(filesDirectory);
            } catch (IOException e) {
                LOGGER.warning(this.messageResource.getForSystem("system.error.directory.notfound", e.toString()));
                throw new BaseException(this.messageResource.get("imagedesign.error.directory.notfound"));
            }
        } else {
            throw new BaseException(this.messageResource.get("imagedesign.error.directory.notfound"));
        }
    }

    public void deleteStoredFile(final String fileName) {
        final Resource resource = getStoredFile(fileName);

        if(resource.exists() && resource.isFile()) {
            try {
                final File storedFile = resource.getFile();
                storedFile.delete();
            } catch (IOException e) {
                LOGGER.warning(this.messageResource.getForSystem("system.error.cannot.delete.file", e.toString()));
                throw new BaseException(this.messageResource.get("imagedesign.error.cannot.delete.file"));
            }
        } else {
            throw new BaseException(this.messageResource.get("imagedesign.error.cannot.delete.file"));
        }
    }

    public Resource getStoredFile(final String fileName) {
        final Path path = this.getPath(fileName);

        if(Objects.isNull(path)) {
            throw new BaseException(this.messageResource.get("imagedesign.error.imagestore.notfound"));
        }

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
