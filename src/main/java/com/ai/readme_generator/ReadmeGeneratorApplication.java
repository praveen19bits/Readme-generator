package com.ai.readme_generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ai.readme_generator.config.FilePatternsConfig;

@SpringBootApplication
@EnableConfigurationProperties(FilePatternsConfig.class)
public class ReadmeGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadmeGeneratorApplication.class, args);
	}
}