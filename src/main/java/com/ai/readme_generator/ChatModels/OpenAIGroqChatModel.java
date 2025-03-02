package com.ai.readme_generator.chatmodels;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OpenAIGroqChatModel implements ChatModel {
    
    private final ChatClient chatClient;

    public OpenAIGroqChatModel(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String generateSystemPromptResponse(String systemPrompt, String prompt) {
        return chatClient.prompt().system(systemPrompt)
                .user(prompt)
                .call()
                .content(); // short for getResult().getOutput().getContent();
    }

    @Override
    public String generatePromptResponse(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content(); // short for getResult().getOutput().getContent();
    }
}

