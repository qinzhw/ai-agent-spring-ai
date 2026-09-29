package com.example.aiagent.service;

import com.example.aiagent.message.SseMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 服务接口
 *
 */
public interface SseService {

    /**
     * 建立 SSE 连接
     *
     * @param chatSessionId 会话 ID，作为连接标识
     * @return SseEmitter 实例
     */
    SseEmitter connect(String chatSessionId);

    /**
     * 向指定会话推送消息
     *
     * @param chatSessionId 会话 ID
     * @param message       SSE 消息
     */
    void send(String chatSessionId, SseMessage message);
}
