package com.example.aiagent.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * SSE 推送消息结构
 * 
 * 用于在 Agent 执行过程中向前端实时推送状态和内容更新。
 */
@Data
@AllArgsConstructor
@Builder
public class SseMessage {

    private Type type;
    private Payload payload;
    private Metadata metadata;

    @Data
    @AllArgsConstructor
    @Builder
    public static class Payload {
        /** 消息内容 */
        private String content;
        /** 角色标识（user / assistant / tool） */
        private String role;
        /** 会话状态文本 */
        private String statusText;
        /** 是否完成 */
        private Boolean done;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Metadata {
        /** 聊天消息 ID */
        private String chatMessageId;
    }

    /**
     * SSE 消息类型
     */
    public enum Type {
        /** AI 生成内容 */
        AI_GENERATED_CONTENT,
        /** AI 流式输出增量片段 */
        AI_STREAMING_DELTA,
        /** AI 规划中 */
        AI_PLANNING,
        /** AI 思考中 */
        AI_THINKING,
        /** AI 执行工具中 */
        AI_EXECUTING,
        /** AI 完成 */
        AI_DONE,
    }
}
