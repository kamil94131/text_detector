package com.image.design.textdetector.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class MessageResource {

    private static final Logger LOGGER = Logger.getLogger(MessageResource.class.getName());
    private final MessageSource messageSource;

    public String get(final String resource, final String... params) {
        try{
            return messageSource.getMessage(resource, params, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            LOGGER.warning(String.format("Resource key not defined in resource file, ex: %s", e.toString()));
            return "";
        }
    }
}
