package com.example.aiagent.config;

import com.example.aiagent.exception.BusinessException;
import com.example.aiagent.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 多模型注册中心
 *
 */
@Slf4j
@Component
public class ChatClientRegistry {

    /**
     * ChatClient 实例注册表
     */
    private final Map<String, ChatClient> chatClients;

    /**
     * 构造函数，Spring 自动注入所有命名 ChatClient Bean
     *
     * @param chatClients ChatClient 实例注册表，键为 Bean 名称，值为 ChatClient 实例
     */
    public ChatClientRegistry(Map<String, ChatClient> chatClients) {
        this.chatClients = Collections.unmodifiableMap(chatClients);
        log.info("ChatClientRegistry 初始化完成，已注册模型：{}", this.chatClients.keySet());
    }

    /**
     * 根据模型标识获取 ChatClient 实例
     *
     * @param key 模型标识，对应 Agent.model 字段
     * @return 对应的 ChatClient 实例
     * @throws BusinessException 若模型未注册
     */
    public ChatClient get(String key) {
        ChatClient client = chatClients.get(key);
        if (client == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,
                    "未注册的模型：" + key + "，当前可用模型：" + chatClients.keySet());
        }
        return client;
    }

    /**
     * 判断是否注册了指定模型
     */
    public boolean contains(String key) {
        return chatClients.containsKey(key);
    }

    /**
     * 返回所有已注册的模型标识
     */
    public Set<String> listModelNames() {
        return chatClients.keySet();
    }
}
