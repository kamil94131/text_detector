package com.image.design.textdetector.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MessageResource {

    private final MessageSource messageSource;

    public String get(final String resource, final String... params) {
        return messageSource.getMessage(resource, params, LocaleContextHolder.getLocale());
    }
}
