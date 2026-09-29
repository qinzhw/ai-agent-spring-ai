package com.example.aiagent.tools;

import com.example.aiagent.agent.tool.RuntimeTool;
import com.example.aiagent.agent.tool.ToolType;
import com.example.aiagent.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 知识库检索工具：供 Agent 调用执行 RAG 语义检索
 */
@Component
@RequiredArgsConstructor
public class KnowledgeTools implements RuntimeTool {

    private final RagService ragService;

    @Override
    public String getName() {
        return "KnowledgeTool";
    }

    @Override
    public String getDescription() {
        return "从指定知识库中执行语义检索（RAG）";
    }

    @Override
    public ToolType getType() {
        return ToolType.OPTIONAL;
    }

    @Tool(
            name = "KnowledgeTool",
            description = "从指定知识库中执行语义检索（RAG）。参数为知识库 ID（kbId）和查询文本（query），返回与查询最相关的知识片段。"
    )
    public String knowledgeQuery(
            @ToolParam(description = "知识库 ID") String kbId,
            @ToolParam(description = "查询文本") String query
    ) {
        List<String> results = ragService.similaritySearch(kbId, query);
        if (results.isEmpty()) {
            return "未找到相关知识内容。";
        }
        return String.join("\n", results);
    }
}
