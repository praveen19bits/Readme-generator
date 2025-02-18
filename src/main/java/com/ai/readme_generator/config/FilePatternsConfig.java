package com.ai.readme_generator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(value = "file-patterns")
public record FilePatternsConfig(List<String> includePatterns, @DefaultValue("") List<String> excludePatterns) {

    public FilePatternsConfig {
        if (includePatterns == null) {
            throw new IllegalArgumentException("includePatterns must not be null");
        }
        // Ensure excludePatterns is never null
        if (excludePatterns == null) {
            excludePatterns = List.of();
        }
    }
}