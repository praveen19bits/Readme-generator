package com.ai.readme_generator.ChatModels;

public interface ChatModel {
    String generatePromptResponse(String prompt);
    String generateSystemPromptResponse(String systemPrompt, String prompt);
}

