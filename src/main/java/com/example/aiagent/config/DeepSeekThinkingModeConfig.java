package com.example.aiagent.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.DefaultToolExecutionEligibilityPredicate;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;

import java.io.IOException;

/**
 * DeepSeek Thinking Mode 禁用配置
 * 
 */
@Slf4j
@Configuration
public class DeepSeekThinkingModeConfig {

    @Bean
    @Primary
    public DeepSeekChatModel deepSeekChatModel(
            @Value("${spring.ai.deepseek.api-key}") String apiKey,
            @Value("${spring.ai.deepseek.base-url:https://api.deepseek.com}") String baseUrl,
            @Value("${spring.ai.deepseek.chat.options.model:deepseek-flash}") String model,
            @Value("${spring.ai.deepseek.chat.options.temperature:0.8}") double temperature,
            ToolCallingManager toolCallingManager,
            RetryTemplate retryTemplate,
            ObjectProvider<ObservationRegistry> observationRegistryProvider) {

        ObservationRegistry observationRegistry = observationRegistryProvider
                .getIfUnique(() -> ObservationRegistry.NOOP);

        // 创建带 thinking mode 拦截器的 DeepSeekApi
        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .baseUrl(baseUrl)
                .apiKey(new SimpleApiKey(apiKey))
                .restClientBuilder(RestClient.builder()
                        .requestInterceptor(new ThinkingModeDisabledInterceptor()))
                .responseErrorHandler(RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER)
                .build();

        log.info("创建自定义 DeepSeekChatModel（已禁用 thinking mode）");

        return DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(org.springframework.ai.deepseek.DeepSeekChatOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .build())
                .toolCallingManager(toolCallingManager)
                .toolExecutionEligibilityPredicate(new DefaultToolExecutionEligibilityPredicate())
                .retryTemplate(retryTemplate)
                .observationRegistry(observationRegistry)
                .build();
    }

    /**
     * RestClient 拦截器：在 DeepSeek API 请求体中注入 "thinking": {"type": "disabled"}
     * 
     */
    static class ThinkingModeDisabledInterceptor implements ClientHttpRequestInterceptor {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public ClientHttpResponse intercept(
                org.springframework.http.HttpRequest request, byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            // 对于无 body 的请求直接放行
            if (body == null || body.length == 0) {
                return execution.execute(request, body);
            }

            try {
                // 解析原始 JSON 并添加 thinking 字段
                JsonNode jsonNode = objectMapper.readTree(body);
                if (jsonNode instanceof ObjectNode) {
                    ObjectNode objectNode = (ObjectNode) jsonNode;
                    ObjectNode thinkingNode = objectMapper.createObjectNode();
                    thinkingNode.put("type", "disabled");
                    objectNode.set("thinking", thinkingNode);
                }
                byte[] modifiedBody = objectMapper.writeValueAsBytes(jsonNode);
                return execution.execute(request, modifiedBody);
            } catch (Exception e) {
                // 解析失败时使用原始 body，避免阻塞请求
                log.warn("修改 DeepSeek 请求体失败，使用原始请求体", e);
                return execution.execute(request, body);
            }
        }
    }
}
