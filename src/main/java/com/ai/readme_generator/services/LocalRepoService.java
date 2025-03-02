package com.ai.readme_generator.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.ai.readme_generator.config.FilePatternsConfig;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service

public class LocalRepoService {

    private static final Logger log = LoggerFactory.getLogger(LocalRepoService.class);

    @Value("${generate.java.summary:true}")
    private boolean generateJavaSummary;

    private Path sourceDir;
    private final List<PathMatcher> includeMatchers;
    private final List<PathMatcher> excludeMatchers;

    public LocalRepoService(FilePatternsConfig config) {
        this.includeMatchers = config.includePatterns().stream()
                .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + normalizePattern(pattern)))
                .collect(Collectors.toList());
        this.excludeMatchers = config.excludePatterns().stream()
                .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + normalizePattern(pattern)))
                .collect(Collectors.toList());
    }

    public String processLocalDirectory(String directoryPath) throws IOException {
        sourceDir = Paths.get(directoryPath).normalize().toAbsolutePath();
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<Path> paths = Files.walk(sourceDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(this::shouldIncludeFile)
                    .forEach(file -> {
                        try {
                            readFileContent(file, contentBuilder);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });
        }

        log.info("Local directory contents generated {}", contentBuilder.toString());
        return contentBuilder.toString();
    }

    private boolean shouldIncludeFile(Path filePath) {
        String relativePath = normalizePath(sourceDir.relativize(filePath));
        if (excludeMatchers.stream().anyMatch(matcher -> matcher.matches(Paths.get(relativePath)))) {
            return false;
        }
        if (includeMatchers.isEmpty()) {
            return true;
        }
        return includeMatchers.stream().anyMatch(matcher -> matcher.matches(Paths.get(relativePath)));
    }

    private void readFileContent(Path file, StringBuilder builder) throws Exception {
        try {
            // Generate summary for Java files if generate summary is enabled
            // Check if file name ends with .java
            String content = "";
            if (generateJavaSummary && file.getFileName().toString().endsWith(".java")) {
                content = CodeSummarizerService.summarizeJavaFile(file.toFile());
            }
            else{
                content = Files.readString(file);
            }
            
            String relativePath = sourceDir.relativize(file).toString();
            builder.append("File: ").append(relativePath).append("\n\n")
                    .append(content).append("\n\n");
        } catch (IOException e) {
            log.error("Error reading file: {}", file, e);
        }
    }

    private String normalizePattern(String pattern) {
        return pattern.trim()
                .replace('\\', '/');
    }

    private String normalizePath(Path path) {
        return path.toString().replace('\\', '/');
    }


}