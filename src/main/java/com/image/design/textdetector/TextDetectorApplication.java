package com.image.design.textdetector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ConfigurationPropertiesScan("com.image.design.textdetector.configuration")
public class TextDetectorApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TextDetectorApplication.class, args);
	}
}
