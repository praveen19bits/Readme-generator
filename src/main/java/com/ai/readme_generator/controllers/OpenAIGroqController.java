package com.ai.readme_generator.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.readme_generator.chatmodels.OpenAIGroqChatModel;

@RestController
public class OpenAIGroqController {
   
    private final OpenAIGroqChatModel chatModel;

    public OpenAIGroqController(OpenAIGroqChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/chatwithgroq")
    public String hello(@RequestParam(value = "message", defaultValue = "Tell me a dad joke") String message) {
        return chatModel.generatePromptResponse(message);
    }

}