import React, { useCallback, useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import AgentChatHistory from "./agentChatView/AgentChatHistory.tsx";
import AgentChatInput from "./agentChatView/AgentChatInput.tsx";
import {
  sendChatMessage,
  getChatMessagesBySessionId,
  getChatSession,
} from "../../api/api.ts";
import { useAgents } from "../../hooks/useAgents.ts";
import EmptyAgentChatView from "./agentChatView/EmptyAgentChatView.tsx";
import type { ChatMessageVO, MessageType, SseMessage, SseMessageType } from "../../types";

const AgentChatView: React.FC = () => {
  const { chatSessionId } = useParams<{ chatSessionId: string }>();
  const { agents } = useAgents();
  const [loading] = useState(false);

  const [messages, setMessages] = useState<ChatMessageVO[]>([]);

  const addMessage = (message: ChatMessageVO) => {
    setMessages((prevMessages) => {
      // 去重：同 ID 消息已存在则替换，避免 SSE 重复推送
      const idx = prevMessages.findIndex((m) => m.id === message.id);
      if (idx >= 0) {
        const updated = [...prevMessages];
        updated[idx] = message;
        return updated;
      }
      return [...prevMessages, message];
    });
  };

  const [agentId, setAgentId] = useState<string>("");
  const agentIdRef = useRef<string>("");

  const getChatMessages = useCallback(async () => {
    if (!chatSessionId) {
      return;
    }
    const resp = await getChatMessagesBySessionId(chatSessionId);
    setMessages(resp.chatMessages);

    const fetchData = async () => {
      const resp = await getChatSession(chatSessionId);
      const id = resp.chatSession.agentId;
      setAgentId(id);
      agentIdRef.current = id;
    };
    fetchData().then();
  }, [chatSessionId]);

  useEffect(() => {
    if (!chatSessionId) {
      return;
    }
    getChatMessages().then();
  }, [chatSessionId, getChatMessages]);

  const handleSendMessage = async (value: string) => {
    const message = value.trim();
    if (!message || !chatSessionId) return;

    // 使用 ref 确保 agentId 已加载
    const currentAgentId = agentIdRef.current;
    if (!currentAgentId) {
      console.warn("agentId 尚未加载，等待...");
      return;
    }

    // 1. 乐观添加用户消息（立即显示）
    const tempUserMsgId = `temp-user-${Date.now()}`;
    setMessages((prev) => [
      ...prev,
      {
        id: tempUserMsgId,
        sessionId: chatSessionId,
        role: "user" as MessageType,
        content: message,
      },
    ]);

    // 2. 调用 API，获取后端保存的用户消息（含真实 ID）
    const savedUserMsg = await sendChatMessage(currentAgentId, {
      sessionId: chatSessionId,
      userInput: message,
    });

    // 3. 用真实 ID 替换临时消息
    setMessages((prev) =>
      prev.map((m) =>
        m.id === tempUserMsgId ? { ...m, id: savedUserMsg.id } : m,
      ),
    );

    // 助手回复通过 SSE 流式推送，无需额外加载
  };

  const [displayAgentStatus, setDisplayAgentStatus] = useState<boolean>(false);
  const [agentStatusText, setAgentStatusText] = useState("");
  const [agentStatusType, setAgentStatusType] = useState<
    SseMessageType | undefined
  >(undefined);

  useEffect(() => {
    // sse 连接处理, 不是对话消息不开连接
    if (!chatSessionId) {
      return;
    }
    const es = new EventSource(
      `http://localhost:8123/api/sse/connect/${chatSessionId}`,
    );
    es.onmessage = (event) => {
      console.log("Received message:", event.data);
    };
    es.onerror = (error) => {
      console.error("SSE error:", error);
    };

    es.addEventListener("message", (event) => {
      // 解析 JSON
      const message = JSON.parse(event.data) as SseMessage;
      if (message.type === "AI_STREAMING_DELTA") {
        // 流式增量 token：追加到对应消息的 content 中
        const token = message.payload.content;
        if (!token) return;
        const msgId = message.metadata?.chatMessageId || "";
        setMessages((prev) => {
          const idx = prev.findIndex((m) => m.id === msgId);
          if (idx >= 0) {
            // 已存在，追加内容
            const updated = [...prev];
            updated[idx] = {
              ...updated[idx],
              content: (updated[idx].content || "") + token,
            };
            return updated;
          } else {
            // 新消息，创建条目
            return [
              ...prev,
              {
                id: msgId,
                sessionId: chatSessionId || "",
                role: "assistant" as MessageType,
                content: token,
              },
            ];
          }
        });
      } else if (message.type === "AI_GENERATED_CONTENT") {
        // 只处理 assistant 角色的消息，tool 消息由数据库加载时转换
        const content = message.payload.content;
        if (!content || message.payload.role !== "assistant") return;
        addMessage({
          id: message.metadata?.chatMessageId || "",
          sessionId: chatSessionId || "",
          role: (message.payload.role || "assistant") as MessageType,
          content,
        });
      } else if (message.type === "AI_PLANNING") {
        setDisplayAgentStatus(true);
        setAgentStatusText(message.payload.statusText);
        setAgentStatusType("AI_PLANNING");
      } else if (message.type === "AI_THINKING") {
        setDisplayAgentStatus(true);
        setAgentStatusText(message.payload.statusText);
        setAgentStatusType("AI_THINKING");
      } else if (message.type === "AI_EXECUTING") {
        setDisplayAgentStatus(true);
        setAgentStatusText(message.payload.statusText);
        setAgentStatusType("AI_EXECUTING");
      } else if (message.type === "AI_DONE") {
        setDisplayAgentStatus(false);
        setAgentStatusText("");
        setAgentStatusType(undefined);
      } else {
        console.warn("Unknown SSE message type:", message.type);
      }
    });

    es.addEventListener("init", (event) => {
      console.log("Received init message:", event.data);
    });

    return () => {
      console.log("Closing SSE connection.");
      es.close();
    };
  }, [chatSessionId]);

  // 如果没有 chatSessionId，显示提示界面
  if (!chatSessionId) {
    return (
      <EmptyAgentChatView
        agents={agents}
        loading={loading}
      />
    );
  }

  // 如果有 chatSessionId，显示正常的聊天界面
  return (
    <div className="flex flex-col h-full">
      <AgentChatHistory
        messages={messages}
        displayAgentStatus={displayAgentStatus}
        agentStatusText={agentStatusText}
        agentStatusType={agentStatusType}
      />
      <div className="border-t border-gray-200 p-4 bg-white">
        <AgentChatInput onSend={handleSendMessage} />
      </div>
    </div>
  );
};

export default AgentChatView;
