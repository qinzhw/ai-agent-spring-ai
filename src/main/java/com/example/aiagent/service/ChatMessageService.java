package com.example.aiagent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.aiagent.model.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {

    /**
     * 按会话 ID 查询最近的 N 条消息
     */
    List<ChatMessage> selectBySessionIdRecently(String sessionId, int limit);
}
