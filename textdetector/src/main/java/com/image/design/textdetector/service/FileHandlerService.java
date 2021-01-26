package com.image.design.textdetector.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.model.file.FileExtension;
import com.image.design.textdetector.model.file.ImageRotation;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class FileHandlerService {

    private static final Logger LOGGER = Logger.getLogger(FileHandlerService.class.getName());
    private final MessageResource messageResource;

    public FileExtension getFileExtension(final MultipartFile multipartFile) {
        final String extension = this.getExtension(multipartFile);

        if(extension.isBlank()) {
            return FileExtension.NOT_ALLOWED;
        }

        return FileExtension.getExtension(extension);
    }

    public String getExtension(final MultipartFile multipartFile) {
        try {
            final String originalFileName = multipartFile.getOriginalFilename();
            return FilenameUtils.getExtension(originalFileName);
        } catch (IllegalArgumentException e) {
            LOGGER.warning(this.messageResource.getForSystem("system.error.file.extension", multipartFile.getOriginalFilename(), e.toString()));
            return "";
        }
    }

    public byte[] getImageWithProperOrientation(final MultipartFile multipartFile, final FileExtension fileExtension) {
        try {
            final byte[] data = this.getMultipartFileBytes(multipartFile);
            final BufferedImage image = this.convertBytesToImage(data);

            if(Objects.isNull(image)) {
                return new byte[0];
            }

            final ImageRotation imageRotation = this.getImageRotation(multipartFile.getInputStream());

            if(imageRotation == ImageRotation.DEGREE_NOT_DETECTED || imageRotation == ImageRotation.DEGREE_0) {
                return data;
            }

            final double radians = Math.toRadians(imageRotation.getRadians());
            final double sin = Math.abs(Math.sin(radians));
            final double cos = Math.abs(Math.cos(radians));
            final double width = Math.floor(image.getWidth() * cos + image.getHeight() * sin);
            final double height = Math.floor(image.getHeight() * cos + image.getWidth() * sin);
            final BufferedImage rotatedImage = new BufferedImage((int)width, (int)height, image.getType());

            final AffineTransform at = new AffineTransform();
            at.translate(width / 2, height / 2);
            at.rotate(radians,0, 0);
            at.translate(-image.getWidth() / 2.0, -image.getHeight() / 2.0);

            final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            rotateOp.filter(image, rotatedImage);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(rotatedImage, fileExtension.name().toLowerCase(), baos);

            return baos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public byte[] getMultipartFileBytes(final MultipartFile file) {
        if(Objects.isNull(file)) {
            return new byte[0];
        }

        try {
            return file.getBytes();
        } catch (IOException e) {
            LOGGER.warning(this.messageResource.getForSystem("system.error.inputfile.read", e.toString()));
            return new byte[0];
        }
    }

    public BufferedImage convertBytesToImage(byte[] data) {
        if(Objects.isNull(data) || data.length == 0) {
            return null;
        }

        try {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            LOGGER.warning(this.messageResource.getForSystem("system.error.image.conversion", e.toString()));
            return null;
        }
    }

    public ImageRotation getImageRotation(final InputStream inputStream) {
        if(Objects.isNull(inputStream)) {
            return ImageRotation.DEGREE_NOT_DETECTED;
        }

        try {
            final Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
            final ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if(Objects.isNull(exifIFD0Directory)) {
                return ImageRotation.DEGREE_0;
            }

            return ImageRotation.getByRotation(exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION));
        } catch (ImageProcessingException | IOException | MetadataException e) {
            return ImageRotation.DEGREE_NOT_DETECTED;
        }
    }
}
