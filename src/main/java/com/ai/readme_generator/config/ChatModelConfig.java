package com.ai.readme_generator.config;

import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatModelConfig {

    @Value("${chat.model}")
    private String chatModelType;

    @Bean
    ChatClient chatModel(OpenAiChatModel openaiGroq, BedrockProxyChatModel awsBedrock) {        
        if ("openaigroq".equalsIgnoreCase(chatModelType)) {
            return ChatClient.create(openaiGroq);
        } else if ("awsbedrock".equalsIgnoreCase(chatModelType)) {
            return ChatClient.create(awsBedrock);
        } else {
            throw new IllegalArgumentException("Invalid chat model type: " + chatModelType);
        }
    }
}

