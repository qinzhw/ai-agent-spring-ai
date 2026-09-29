package com.example.aiagent.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 Re2 Advisor
 * 可提高大型语言模型的推理能力
 */
public class ReReadingAdvisor implements CallAdvisor, StreamAdvisor {

    /**
     * 执行请求前，改下Prompt
     */
    private ChatClientRequest before(ChatClientRequest request) {
        String userText = request.prompt().getUserMessage() != null
                ? request.prompt().getUserMessage().getText() : "";

        Map<String, Object> newContext = new HashMap<>(request.context());
        newContext.put("re2_input_query", userText);

        String newText = """
                %s
                Read the question again: %s
                """.formatted(userText, userText);

        Prompt newPrompt = request.prompt().augmentUserMessage(
                msg -> UserMessage.builder().text(newText).build());

        return request.mutate()
                .prompt(newPrompt)
                .context(newContext)
                .build();
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        return chain.nextCall(this.before(request));
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        return chain.nextStream(this.before(request));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

}
