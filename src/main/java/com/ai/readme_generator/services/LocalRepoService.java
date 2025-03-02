package com.ai.readme_generator.services;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ai.readme_generator.config.FilePatternsConfig;

@Service
public class LocalRepoService {

    private static final Logger log = LoggerFactory.getLogger(LocalRepoService.class);

    @Value("${generate.java.summary:false}")
    private boolean generateJavaSummary;

    private Path sourceDir;
    private List<PathMatcher> includeMatchers;
    private List<PathMatcher> excludeMatchers;

    // public LocalRepoService(FilePatternsConfig config) {
    //     this.includeMatchers = config.includePatterns().stream()
    //             .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + normalizePattern(pattern)))
    //             .collect(Collectors.toList());
    //     this.excludeMatchers = config.excludePatterns().stream()
    //             .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + normalizePattern(pattern)))
    //             .collect(Collectors.toList());
    // }

    public void setFilePatternsConfig(FilePatternsConfig config){
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

    /**
     * Determines whether a given file should be included based on inclusion and exclusion patterns.
     *
     * @param filePath the path of the file to check
     * @return true if the file should be included, false otherwise
    */
    private boolean shouldIncludeFile(Path filePath) {
        String relativePath = normalizePath(sourceDir.relativize(filePath));
        // Exclude the file if it matches any of the exclude patterns
        if (excludeMatchers.stream().anyMatch(matcher -> matcher.matches(Paths.get(relativePath)))) {
            return false;
        }
        // If no include patterns are specified, include all files
        if (includeMatchers.isEmpty()) {
            return true;
        }

        if (includeMatchers.stream().anyMatch(matcher -> matcher.matches(Paths.get(relativePath)))) {
            System.out.println("Included File: " + relativePath);
        }
        // Include the file if it matches any of the include patterns
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