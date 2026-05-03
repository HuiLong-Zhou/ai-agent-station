package com.zhl.domain.agent.service.armory;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson2.JSON;
import com.zhl.domain.agent.model.entity.ArmoryCommandEntity;
import com.zhl.domain.agent.model.valobj.*;
import com.zhl.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  ai agent 客户端对话对象节点
 * @author zhl
 * @since 2026-05-02 22:20
 */
@Slf4j
@Service
public class AiClientNode extends AbstractArmorySupport{

    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 构建，client {}", JSON.toJSONString(requestParameter));
        List<AiClientVO> aiClientList = dynamicContext.getValue(dataName());

        if(aiClientList == null || aiClientList.isEmpty()) {
            log.warn("没有需要被初始化的 ai client");
            return router(requestParameter, dynamicContext);
        }

        StringBuilder defaultSystem = new StringBuilder("Ai 智能体\r\n");

        // 获取提示词信息
        Map<String, AiClientSystemPromptVO> systemPromptMap = dynamicContext.getValue(AiAgentEnumVO.AI_CLIENT_SYSTEM_PROMPT.getDataName());

        for (AiClientVO aiClientVO : aiClientList) {
            // 1. 拼接提示词信息
            for (String promptId : aiClientVO.getPromptIdList()) {
                AiClientSystemPromptVO systemPromptVO = systemPromptMap.get(promptId);
                defaultSystem.append(systemPromptVO.getPromptContent());
            }

            // 2. 从容器中获取 model
            OpenAiChatModel chatModel = getBean(aiClientVO.getModelBeanName());

            // 3. 从容器中获取 mcp
            List<McpSyncClient> mcpSyncClientList = new ArrayList<>();
            for (String mcpBeanName : aiClientVO.getMCPBeanName()) {
                mcpSyncClientList.add(getBean(mcpBeanName));
            }

            // 4. 从容器中获取 advisor
            List<Advisor> advisorList = new ArrayList<>();
            for (String advisorBeanName : aiClientVO.getAdvisorBeanName()) {
                advisorList.add(getBean(advisorBeanName));
            }

            // 5. 组装构建 ChatClient
            ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem(defaultSystem.toString())
                .defaultToolCallbacks(mcpSyncClientList.toArray(new SyncMcpToolCallbackProvider[]{}))
                .defaultAdvisors(advisorList.toArray(new Advisor[]{})).build();

            // 6. 注册 client Bean对象
            registerBean(beanName(aiClientVO.getClientId()), ChatClient.class, chatClient);

        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }

    @Override
    protected String dataName() {
        return AiAgentEnumVO.AI_CLIENT.getDataName();
    }

    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT.getBeanName(beanId);
    }
}
