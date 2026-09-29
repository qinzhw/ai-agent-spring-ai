package com.example.aiagent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多模型 ChatClient 配置
 *
 */
@Configuration
public class MultiChatClientConfig {

    /**
     * DeepSeek 模型 ChatClient
     * Bean 名称 "deepseek-flash" 对应 Agent.model 字段值
     */
    @Bean("deepseek-flash")
    public ChatClient deepSeekChatClient(DeepSeekChatModel deepSeekChatModel) {
        return ChatClient.create(deepSeekChatModel);
    }

    /**
     * 智谱 GLM 模型 ChatClient
     * Bean 名称 "glm-5.3-flash" 对应 Agent.model 字段值
     */
    @Bean("glm-5.3-flash")
    public ChatClient zhiPuAiChatClient(ZhiPuAiChatModel zhiPuAiChatModel) {
        return ChatClient.create(zhiPuAiChatModel);
    }
}
