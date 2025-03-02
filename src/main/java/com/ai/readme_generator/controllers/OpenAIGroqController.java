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

import com.ai.readme_generator.ChatModels.OpenAIGroqChatModel;

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