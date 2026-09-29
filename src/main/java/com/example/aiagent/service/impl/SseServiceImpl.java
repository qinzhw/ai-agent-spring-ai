package com.example.aiagent.service.impl;

import cn.hutool.json.JSONUtil;
import com.example.aiagent.message.SseMessage;
import com.example.aiagent.service.SseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SSE 服务实现
 *
 */
@Slf4j
@Service
@AllArgsConstructor
public class SseServiceImpl implements SseService {

    private final ConcurrentMap<String, SseEmitter> clients = new ConcurrentHashMap<>();

    @Override
    public SseEmitter connect(String chatSessionId) {
        // 超时 30 分钟
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        clients.put(chatSessionId, emitter);

        try {
            emitter.send(SseEmitter.event()
                    .name("init")
                    .data("connected"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        emitter.onCompletion(() -> {
            clients.remove(chatSessionId);
            log.debug("SSE 连接关闭（完成）: {}", chatSessionId);
        });
        emitter.onTimeout(() -> {
            clients.remove(chatSessionId);
            log.debug("SSE 连接关闭（超时）: {}", chatSessionId);
        });
        emitter.onError((error) -> {
            clients.remove(chatSessionId);
            log.debug("SSE 连接关闭（异常）: {}", chatSessionId);
        });

        return emitter;
    }

    @Override
    public void send(String chatSessionId, SseMessage message) {
        SseEmitter emitter = clients.get(chatSessionId);
        if (emitter != null) {
            try {
                String jsonStr = JSONUtil.toJsonStr(message);
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(jsonStr));
            } catch (IOException e) {
                log.error("SSE 消息发送失败: {}", chatSessionId, e);
            }
        } else {
            log.warn("SSE 连接不存在: {}", chatSessionId);
        }
    }
}
