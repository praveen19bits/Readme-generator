package com.ai.readme_generator.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.readme_generator.ChatModels.AWSBedrockChatModel;

@RestController
public class AWSBedRockController {

    private final AWSBedrockChatModel chatModel;
   
    public AWSBedRockController(AWSBedrockChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/chat")
    public String hello(@RequestParam(value = "message", defaultValue = "Tell me a dad joke") String message) {
        return chatModel.generatePromptResponse(message);
    }
}
