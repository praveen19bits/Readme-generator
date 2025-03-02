package com.ai.readme_generator.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.readme_generator.chatmodels.AWSBedrockChatModel;
import com.ai.readme_generator.chatmodels.OpenAIGroqChatModel;
import com.ai.readme_generator.config.FilePatternsConfig;
import com.ai.readme_generator.dto.ReadMeRequest;
import com.ai.readme_generator.services.ContentGeneratorService;
import com.ai.readme_generator.services.LocalRepoService;


@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Allow frontend React app
public class ReadMeGeneratorController{
    private static final Logger log = LoggerFactory.getLogger(ReadMeGeneratorController.class);

    @Value("classpath:/prompts/readme.st")
    private Resource readmePrompt;

    @Value("${chat.model}")
    private String chatModelType;

    private final AWSBedrockChatModel awsBedrockChatModel;

    private final OpenAIGroqChatModel openAIGroqChatModel;

    private final ContentGeneratorService contentGeneratorService;

    private final FilePatternsConfig readmeConfig;

    private final LocalRepoService localFileService;

    public ReadMeGeneratorController(LocalRepoService localFileService,
                                            ContentGeneratorService contentGeneratorService,
                                            AWSBedrockChatModel awsBedrockChatModel,
                                            OpenAIGroqChatModel openAIGroqChatModel,
                                            FilePatternsConfig readmeConfig) {
        this.localFileService = localFileService;
        this.contentGeneratorService = contentGeneratorService;
        this.awsBedrockChatModel = awsBedrockChatModel;
        this.openAIGroqChatModel = openAIGroqChatModel;
        this.readmeConfig = readmeConfig;
    }

    @PostMapping("/local")
    public ResponseEntity<String> generateReadmeForLocal(@RequestBody ReadMeRequest request) {
        FilePatternsConfig finalPatterns = resolvePatterns(request.readConfig(), readmeConfig);
        localFileService.setFilePatternsConfig(finalPatterns);
        String readme = generateReadme(request.pathOrRepoUrl());
        return ResponseEntity.ok(readme);
    }

    @PostMapping("/github")
    public ResponseEntity<String> generateReadmeForGithub(@RequestBody ReadMeRequest request) {
        return ResponseEntity.ok("OK");
    }

    private FilePatternsConfig resolvePatterns(FilePatternsConfig userPatterns, FilePatternsConfig defaultPatterns) {
        if (userPatterns != null) {
            // If user-provided config is missing exclude patterns, use default exclude patterns
            if((userPatterns.excludePatterns() == null || userPatterns.excludePatterns().isEmpty())){
                userPatterns = new FilePatternsConfig(userPatterns.includePatterns(), defaultPatterns.excludePatterns());
            }
            return userPatterns; // Use UI-provided config if available
        }
        return defaultPatterns; // Otherwise, fallback to application.yaml config
    }

    private String generateReadme(@RequestParam(required = false) String localPath) {
        try {
            String content = contentGeneratorService.generateContent(localPath);
            String generarteReadMeStr = "";
            if ("openaigroq".equalsIgnoreCase(chatModelType)) {
                generarteReadMeStr = openAIGroqChatModel.generateSystemPromptResponse(readmePrompt.toString(), content);
            } else if ("awsbedrock".equalsIgnoreCase(chatModelType)) {
                generarteReadMeStr = awsBedrockChatModel.generateSystemPromptResponse(readmePrompt.toString(), content);
            } 
            return generarteReadMeStr;
        } catch (Exception e) {
            log.error("Error generating content", e);
            return "Error generating content: " + e.getMessage();
        }
    }
}