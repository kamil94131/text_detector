package com.image.design.textdetector;

import nu.pattern.OpenCV;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.image.design.textdetector.configuration")
public class TextDetectorApplication {

	public static void main(String[] args) {
		OpenCV.loadLocally();
		SpringApplication.run(TextDetectorApplication.class, args);
	}
}
