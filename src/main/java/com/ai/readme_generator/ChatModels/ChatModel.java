package com.ai.readme_generator.chatmodels;
public interface ChatModel {
    String generatePromptResponse(String prompt);
    String generateSystemPromptResponse(String systemPrompt, String prompt);
}

