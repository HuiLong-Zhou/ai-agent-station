package com.zhl.domain.agent.service.execute.fixed;

import com.zhl.domain.agent.adapter.repository.IAgentRepository;
import com.zhl.domain.agent.model.entity.ExecuteCommandEntity;
import com.zhl.domain.agent.model.valobj.AiAgentClientFlowConfigVO;
import com.zhl.domain.agent.model.valobj.enums.AiAgentEnumVO;
import com.zhl.domain.agent.service.IExecuteStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  固定执行策略
 * @author zhl
 * @since 2026-05-19 21:57
 */
@Slf4j
@Service("fixedAgentExecuteStrategy")
public class FixedAgentExecuteStrategy implements IExecuteStrategy {

    @Resource
    private IAgentRepository repository;

    @Resource
    protected ApplicationContext applicationContext;

    public static final String CHAT_MEMORY_CONVERSATION_ID_KEY = "chat_memory_conversation_id";
    public static final String CHAT_MEMORY_RETRIEVE_SIZE_KEY = "chat_memory_response_size";

    @Override
    public void execute(ExecuteCommandEntity requestParameter, ResponseBodyEmitter emitter) throws Exception {
        // 1. 获取配置客户端
        List<AiAgentClientFlowConfigVO> aiAgentClientList = repository.queryAiAgentClientsByAgentId(requestParameter.getAiAgentId());

        // 2. 循环执行客户端
        String content = "";

        for (AiAgentClientFlowConfigVO config : aiAgentClientList) {
            ChatClient chatClient = getChatClientByClientId(config.getClientId());

            content = chatClient.prompt(requestParameter.getMessage() + "，" + content)
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .advisors(a -> a
                    .param(CHAT_MEMORY_CONVERSATION_ID_KEY, requestParameter.getSessionId())
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call().content();

            log.info("智能体对话进行，客户端ID {}", requestParameter.getAiAgentId());
        }

        log.info("智能体对话请求，结果 {} {}", requestParameter.getAiAgentId(), content);
    }

    private ChatClient getChatClientByClientId(String clientId) {
        return getBean(AiAgentEnumVO.AI_CLIENT.getBeanName(clientId));
    }

    private <T> T getBean(String beanName) {
        return (T) applicationContext.getBean(beanName);
    }

}
