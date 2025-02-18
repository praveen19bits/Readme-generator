package com.ai.readme_generator.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AWSBedRockController {
   
    @Value("classpath:/prompts/readme.st")
    private Resource readmePrompt;

    private final ChatClient chatClient;

    public AWSBedRockController(@Qualifier("awsBedRockConverseChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat/aws")
    public String hello(@RequestParam(value = "message", defaultValue = "Tell me a dad joke") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content(); // short for getResult().getOutput().getContent();
    }

    public String callAwsBedRock(String sourceCode) {
        return chatClient.prompt().system(readmePrompt)
                .user("Here is the source code:\n\n" + sourceCode)
                .call()
                .content(); // short for getResult().getOutput().getContent();
    }
}