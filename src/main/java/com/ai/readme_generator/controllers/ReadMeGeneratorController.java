package com.ai.readme_generator.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ai.readme_generator.ChatModels.AWSBedrockChatModel;
import com.ai.readme_generator.ChatModels.ChatModel;
import com.ai.readme_generator.ChatModels.OpenAIGroqChatModel;
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

    public ReadMeGeneratorController(LocalRepoService localFileService,
                                            ContentGeneratorService contentGeneratorService,
                                            AWSBedrockChatModel awsBedrockChatModel,
                                            OpenAIGroqChatModel openAIGroqChatModel) {
        this.contentGeneratorService = contentGeneratorService;
        this.awsBedrockChatModel = awsBedrockChatModel;
        this.openAIGroqChatModel = openAIGroqChatModel;
    }

    @PostMapping("/readme")
    @ResponseBody
    public ResponseEntity<String> generateReadme(@RequestParam(required = false) String localPath) {

        try {
            String content = contentGeneratorService.generateContent(localPath);
            String generarteReadMeStr = "";
            if ("openaigroq".equalsIgnoreCase(chatModelType)) {
                generarteReadMeStr = openAIGroqChatModel.generateSystemPromptResponse(readmePrompt.toString(), content);
            } else if ("awsbedrock".equalsIgnoreCase(chatModelType)) {
                generarteReadMeStr = awsBedrockChatModel.generateSystemPromptResponse(readmePrompt.toString(), content);
            } 
            return ResponseEntity.ok(generarteReadMeStr.toString());
        } catch (Exception e) {
            log.error("Error generating content", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating content: " + e.getMessage());
        }
    }
}