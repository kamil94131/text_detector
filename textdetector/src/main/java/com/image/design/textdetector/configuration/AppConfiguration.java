package com.image.design.textdetector.configuration;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class AppConfiguration {

    private static final String LANG_PARAM = "lang";
    private static final String MASSAGES_CLASSPATH = "classpath:i18n/messages";
    private static final String ENCODING = "UTF-8";

    @Value("${tesseract.default.language}")
    private String tesseractDefaultLanguage;

    @Bean
    @Qualifier("frozenEastNN")
    public ClassPathResource frozeEastNN() {
        return new ClassPathResource("target/classes/opencv/frozen_east_text_detection.pb");
    }

    @Bean
    public Tesseract tesseract() {
        final Tesseract tesseract = new Tesseract();
        tesseract.setLanguage(this.tesseractDefaultLanguage);
        return tesseract;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(LocaleContextHolder.getLocale());
        return sessionLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(LANG_PARAM);
        return localeChangeInterceptor;
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource bundleMessageSource = new ReloadableResourceBundleMessageSource();
        bundleMessageSource.setBasename(MASSAGES_CLASSPATH);
        bundleMessageSource.setDefaultEncoding(ENCODING);
        return bundleMessageSource;
    }
}
