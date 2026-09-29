package com.example.aiagent.service.impl;

import com.example.aiagent.config.RagProperties;
import com.example.aiagent.model.entity.Chunk;
import com.example.aiagent.service.ChunkService;
import com.example.aiagent.service.RagService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * RAG 服务实现：通过 Ollama bge-m3 模型嵌入 + pgvector L2 相似度检索
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {

    private final RagProperties ragProperties;
    private final ChunkService chunkService;
    private final WebClient.Builder webClientBuilder;

    private WebClient webClient;

    /**
     * 懒加载 WebClient，避免启动时连接 Ollama
     */
    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.baseUrl(ragProperties.getBaseUrl()).build();
        }
        return webClient;
    }

    @Data
    private static class EmbeddingResponse {
        private float[] embedding;
    }

    @Override
    public float[] embed(String text) {
        EmbeddingResponse resp = getWebClient().post()
                .uri("/api/embeddings")
                .bodyValue(Map.of(
                        "model", ragProperties.getEmbeddingModel(),
                        "prompt", text
                ))
                .retrieve()
                .bodyToMono(EmbeddingResponse.class)
                .block();
        Assert.notNull(resp, "Embedding response cannot be null");
        return resp.getEmbedding();
    }

    @Override
    public List<String> similaritySearch(String kbId, String query) {
        float[] queryEmbedding = embed(query);
        String vectorLiteral = toPgVector(queryEmbedding);
        List<Chunk> chunks = chunkService.similaritySearch(kbId, vectorLiteral, ragProperties.getSimilarityTopK());
        return chunks.stream().map(Chunk::getContent).toList();
    }

    /**
     * 将 float[] 转为 pgvector 文本格式 "[0.1,0.2,...]"
     */
    private String toPgVector(float[] v) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < v.length; i++) {
            sb.append(v[i]);
            if (i < v.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
