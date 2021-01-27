package com.image.design.textdetector.configuration;

import net.sourceforge.tess4j.Tesseract;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Configuration
public class AppConfiguration {

    private static final Logger LOGGER = Logger.getLogger(AppConfiguration.class.getName());
    private static final String LANG_PARAM = "lang";
    private static final String ENCODING = "UTF-8";

    @Value("${tesseract.default.language}")
    private String tesseractLang;

    @Value("${messages.path}")
    private String messagesPath;

    @Value("${tesseract.file.path}")
    private String tesseractFilePath;

    @Value("${tesseract.directory.path}")
    private String tesseractDirectoryPath;

    @Value("${tesseract.segmentation.mode}")
    private int tesseractSegmentationMode;

    @Bean
    public Net frozeEastNN(final ResourceLoader resourceLoader) {
        final Resource neuralNetworkResource = resourceLoader.getResource(this.tesseractFilePath);
        final String neuralNetworkPath = getFrozenEastNeuralNetworkPath(neuralNetworkResource);
        return Dnn.readNetFromTensorflow(neuralNetworkPath);
    }

    private String getFrozenEastNeuralNetworkPath(final Resource neuralNetworkResource) {
        try {
            final File frozenEastNNFile = neuralNetworkResource.getFile();
            if(!frozenEastNNFile.exists()) {
                LOGGER.warning("Couldn't find frozen east file in resources");
                return null;
            }
            return frozenEastNNFile.getPath();
        } catch (IOException e) {
            LOGGER.warning(String.format("Couldn't find frozen east file in resources, ex: %s", e.toString()));
            return null;
        }
    }

    @Bean
    public Tesseract tesseract(final ResourceLoader resourceLoader) throws IOException {
        final Tesseract tesseract = new Tesseract();
        tesseract.setLanguage(this.tesseractLang);
        tesseract.setPageSegMode(this.tesseractSegmentationMode);
        tesseract.setDatapath(resourceLoader.getResource(this.tesseractDirectoryPath).getFile().getPath());
        return tesseract;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(LANG_PARAM);
        return localeChangeInterceptor;
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        final ReloadableResourceBundleMessageSource bundleMessageSource = new ReloadableResourceBundleMessageSource();
        bundleMessageSource.setBasename(this.messagesPath);
        bundleMessageSource.setDefaultEncoding(ENCODING);
        return bundleMessageSource;
    }
}
