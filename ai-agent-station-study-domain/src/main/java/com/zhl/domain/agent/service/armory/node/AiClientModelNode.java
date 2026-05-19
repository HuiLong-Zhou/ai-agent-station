package com.zhl.domain.agent.service.armory.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson2.JSON;
import com.zhl.domain.agent.model.entity.ArmoryCommandEntity;
import com.zhl.domain.agent.model.valobj.enums.AiAgentEnumVO;
import com.zhl.domain.agent.model.valobj.AiClientModelVO;
import com.zhl.domain.agent.service.armory.node.factory.DefaultArmoryStrategyFactory;
import io.modelcontextprotocol.client.McpSyncClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 对话模型节点配置
 * @author zhl
 * @since 2026-05-02 13:35
 */
@Slf4j
@Service
public class AiClientModelNode extends AbstractArmorySupport{

    @Resource
    private AiClientAdvisorNode aiClientAdvisorNode;

    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {

        log.info("Ai Agent 构建，Model 对话模型 {}", JSON.toJSONString(requestParameter));
        List<AiClientModelVO> aiClientModelVOList = dynamicContext.getValue(dataName());

        if(aiClientModelVOList == null || aiClientModelVOList.isEmpty()) {
            log.warn("没有需要被初始化的 ai client model");
            return router(requestParameter, dynamicContext);
        }

        for (AiClientModelVO aiClientModelVO : aiClientModelVOList) {

            String apiId = aiClientModelVO.getApiId();
            // 从 Spring 容器中获取之前注册的 api 对象
            // 获取当前模型关联的 API Bean 对象
            OpenAiApi openAiApi = getBean(AiAgentEnumVO.AI_CLIENT_API.getBeanName(apiId));

            if (null == openAiApi) {
                throw new RuntimeException("mode 2 api is null");
            }

            List<McpSyncClient> mcpSyncClients = new ArrayList<>();

            for (String mcpId : aiClientModelVO.getToolMcpIds()) {
                // 从 Spring 容器中获取之前注册的 mcp 对象
                // 获取当前模型关联的 Tool MCP Bean 对象
                McpSyncClient mcpSyncClient = getBean(AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getBeanName(mcpId));
                mcpSyncClients.add(mcpSyncClient);
            }
            // 实例化对话模型（如果有其他模型对接，可以使用 one-api 服务，转换为 openai 模型格式）
            OpenAiChatModel aiChatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(
                    OpenAiChatOptions.builder()
                        .model(aiClientModelVO.getModelName())
                        .toolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients).getToolCallbacks())
                        .build()
                ).build();

            // 注册 Bean 对象
            registerBean(beanName(aiClientModelVO.getModelId()), OpenAiChatModel.class, aiChatModel);

        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientAdvisorNode;
    }

    @Override
    protected String dataName() {
        return AiAgentEnumVO.AI_CLIENT_MODEL.getDataName();
    }

    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT_MODEL.getBeanName(beanId);
    }
}
