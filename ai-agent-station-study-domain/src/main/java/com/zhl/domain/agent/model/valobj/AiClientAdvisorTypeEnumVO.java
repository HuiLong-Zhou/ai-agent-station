package com.zhl.domain.agent.model.valobj;

import com.zhl.domain.agent.service.armory.factory.element.RagAnswerAdvisor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  顾问类型枚举
 * @author zhl
 * @since 2026-05-02 22:30
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AiClientAdvisorTypeEnumVO {

    CHAT_MEMORY("ChatMemory", "上下文记忆(内存模式)") {
        @Override
        public Advisor createAdvisor(AiClientAdvisorVO aiClientAdvisorVO, VectorStore vectorStore) {
            return PromptChatMemoryAdvisor.builder(
                MessageWindowChatMemory.builder()
                    .maxMessages(aiClientAdvisorVO.getChatMemory().getMaxMessages())
                    .build()
            ).build();
        }
    },

    RAW_ANSWER("RagAnswer", "知识库") {
        @Override
        public Advisor createAdvisor(AiClientAdvisorVO aiClientAdvisorVO, VectorStore vectorStore) {
            return new RagAnswerAdvisor(vectorStore, SearchRequest.builder()
                .topK(aiClientAdvisorVO.getRagAnswer().getTopK())
                .filterExpression(aiClientAdvisorVO.getRagAnswer().getFilterExpression())
                .build());
        }
    },
    ;

    private String code;

    private String info;

    public abstract Advisor createAdvisor(AiClientAdvisorVO aiClientAdvisorVO, VectorStore vectorStore);

    private static final Map<String, AiClientAdvisorTypeEnumVO> CODE_MAP = new HashMap<>();


    static {
        for(AiClientAdvisorTypeEnumVO advisorTypeEnumVO : AiClientAdvisorTypeEnumVO.values()) {
            CODE_MAP.put(advisorTypeEnumVO.code, advisorTypeEnumVO);
        }
    }

    /**
     * 根据code获取对应的枚举
     *
     * @param code 枚举code值
     * @return 对应的枚举，如果未找到则返回null
     */
    public static AiClientAdvisorTypeEnumVO getByCode(String code) {
        if(code == null) {
            return null;
        }
        AiClientAdvisorTypeEnumVO result = CODE_MAP.get(code);
        if(result == null) {
            throw new RuntimeException("code value " + code + " not exist!");
        }
        return result;
    }

}
