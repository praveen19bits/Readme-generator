package com.ai.readme_generator.controllers;

import java.io.IOException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class GroqOpenAIController {
   
    private final ChatClient chatClient;

    @Value("classpath:/prompts/readme.st")
    private Resource readmePrompt;

    public GroqOpenAIController(@Qualifier("openAIChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String hello(@RequestParam(value = "message", defaultValue = "Tell me a dad joke") String message) {
        return chatClient.prompt().system("You are helpul assistan on Cricket. Summarize user response in 1 line. Other than cricker, please response- I can assist on Cricket Queris only, thanks.")
                .user(message)
                .call()
                .content(); // short for getResult().getOutput().getContent();
    }

    @PostMapping("/readme")
    public String generarteReadMe(@RequestBody MultipartFile file) {

        try{
            // Read the source code file
            String sourceCode = new String(file.getBytes());
            return chatClient.prompt().system(readmePrompt)
                    .user("Here is the source code:\n\n" + sourceCode)
                    .call()
                    .content(); // short for getResult().getOutput().getContent();
        }
        catch (IOException e){
            throw new RuntimeException("Error reading file", e);
        }
    }

    public String generarteReadMeStr(String sourceCode) {
        return chatClient.prompt().system(readmePrompt)
                .user("Here is the source code:\n\n" + sourceCode)
                .call()
                .content(); // short for getResult().getOutput().getContent();
    }
}