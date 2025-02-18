package com.ai.readme_generator.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class ContentGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(ContentGeneratorService.class);

    private final LocalRepoService localFileService;

    public ContentGeneratorService(LocalRepoService localFileService) {
        this.localFileService = localFileService;
    }

    public String generateContent(String localPath) throws Exception {
        if (localPath != null && !localPath.isBlank()) {
            log.info("Processing local path: {}", localPath);
            return localFileService.processLocalDirectory(localPath);
            
        } else {
            throw new IllegalArgumentException("Local path must be provided");
        }
    }
}