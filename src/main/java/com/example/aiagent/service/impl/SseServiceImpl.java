package com.example.aiagent.service.impl;

import cn.hutool.json.JSONUtil;
import com.example.aiagent.message.SseMessage;
import com.example.aiagent.service.SseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SSE 服务实现
 *
 * 支持消息缓冲：在 SSE 连接建立之前发送的消息会被缓存，
 * 连接建立后自动 flush。
 */
@Slf4j
@Service
@AllArgsConstructor
public class SseServiceImpl implements SseService {

    private final ConcurrentMap<String, SseEmitter> clients = new ConcurrentHashMap<>();

    /** 缓冲队列：SSE 连接建立前暂存消息 */
    private final ConcurrentMap<String, List<SseMessage>> pendingBuffers = new ConcurrentHashMap<>();

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

        // flush 缓冲消息
        flushBuffer(chatSessionId, emitter);

        emitter.onCompletion(() -> {
            clients.remove(chatSessionId);
            pendingBuffers.remove(chatSessionId);
            log.debug("SSE 连接关闭（完成）: {}", chatSessionId);
        });
        emitter.onTimeout(() -> {
            clients.remove(chatSessionId);
            pendingBuffers.remove(chatSessionId);
            log.debug("SSE 连接关闭（超时）: {}", chatSessionId);
        });
        emitter.onError((error) -> {
            clients.remove(chatSessionId);
            pendingBuffers.remove(chatSessionId);
            log.debug("SSE 连接关闭（异常）: {}", chatSessionId);
        });

        return emitter;
    }

    @Override
    public void send(String chatSessionId, SseMessage message) {
        SseEmitter emitter = clients.get(chatSessionId);
        if (emitter != null) {
            doSend(chatSessionId, emitter, message);
        } else {
            // SSE 连接尚未建立，放入缓冲队列
            log.info("SSE 连接未建立，消息暂存缓冲: {}", chatSessionId);
            pendingBuffers
                    .computeIfAbsent(chatSessionId, k -> new CopyOnWriteArrayList<>())
                    .add(message);
        }
    }

    /**
     * 实际发送 SSE 消息
     */
    private void doSend(String chatSessionId, SseEmitter emitter, SseMessage message) {
        try {
            String jsonStr = JSONUtil.toJsonStr(message);
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(jsonStr));
        } catch (IOException e) {
            log.error("SSE 消息发送失败: {}", chatSessionId, e);
        }
    }

    /**
     * 将缓冲队列中的消息全部发送给已连接的客户端
     */
    private void flushBuffer(String chatSessionId, SseEmitter emitter) {
        List<SseMessage> buffer = pendingBuffers.remove(chatSessionId);
        if (buffer != null && !buffer.isEmpty()) {
            log.info("flush 缓冲消息 {} 条: {}", buffer.size(), chatSessionId);
            for (SseMessage msg : buffer) {
                doSend(chatSessionId, emitter, msg);
            }
        }
    }
}
