package com.example.finrag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class RagConfig {

    @Bean
    public ChatClient financialChatClient(ChatClient.Builder builder,
                                          @Value("classpath:prompts/system-guardrail.st") Resource systemPrompt) {
        return builder.defaultSystem(systemPrompt).build();
    }

    @Bean
    public TokenTextSplitter tokenTextSplitter(RagProperties properties) {
        return TokenTextSplitter.builder()
                .withChunkSize(properties.chunkSize())
                .build();
    }
}