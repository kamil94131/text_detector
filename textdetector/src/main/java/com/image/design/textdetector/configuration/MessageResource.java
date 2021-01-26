package com.image.design.textdetector.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class MessageResource {

    private static final Logger LOGGER = Logger.getLogger(MessageResource.class.getName());
    private final MessageSource messageSource;

    public String get(final String resource, final String... params) {
        return this.get(resource, LocaleContextHolder.getLocale(), params);
    }

    public String getForSystem(final String resource, final String... params) {
        return this.get(resource, Locale.US, params);
    }

    public String get(final String resource, final Locale locale, final String... params) {
        try{
            return this.messageSource.getMessage(resource, params, locale);
        } catch (NoSuchMessageException e) {
            LOGGER.warning(this.messageSource.getMessage("system.error.resource.notfound", new Object[] {e.toString() }, Locale.US));
            return "";
        }
    }
}
