package com.example.aiagent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aiagent.mapper.ChatMessageMapper;
import com.example.aiagent.model.entity.ChatMessage;
import com.example.aiagent.service.ChatMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Override
    public List<ChatMessage> selectBySessionIdRecently(String sessionId, int limit) {
        return baseMapper.selectBySessionIdRecently(sessionId, limit);
    }
}
