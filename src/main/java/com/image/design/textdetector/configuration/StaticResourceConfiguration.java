package com.image.design.textdetector.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    private static final String CLASSPATH_RESOURCE_LOCATIONS = "file:public/images/";
    private static final String DEFAULT_SERVLET_NAME = "dispatcherServlet";
    private static final String RESOURCE_HANDLER_PATH = "public/images/**";

    private final ResourceLoader resourceLoader;

    public StaticResourceConfiguration(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable(DEFAULT_SERVLET_NAME);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(RESOURCE_HANDLER_PATH)
                .addResourceLocations(this.resourceLoader.getResource(CLASSPATH_RESOURCE_LOCATIONS));
    }
}
