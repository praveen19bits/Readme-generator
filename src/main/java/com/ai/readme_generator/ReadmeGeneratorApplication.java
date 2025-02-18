package com.ai.readme_generator;

import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.ai.readme_generator.config.FilePatternsConfig;

@SpringBootApplication
@EnableConfigurationProperties(FilePatternsConfig.class)
public class ReadmeGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadmeGeneratorApplication.class, args);
	}

	@Bean
	ChatClient openAIChatClient(OpenAiChatModel chatModel) {
		return ChatClient.create(chatModel);
	}
	

	//Inject a bean for aws bedrock converse chat model using ChatClient.create
	@Bean
	ChatClient awsBedRockConverseChatClient(BedrockProxyChatModel chatModel) {
		return ChatClient.create(chatModel);
	}
	

}