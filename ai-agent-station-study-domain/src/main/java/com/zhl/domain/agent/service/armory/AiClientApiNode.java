package com.zhl.domain.agent.service.armory;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson2.JSON;
import com.zhl.domain.agent.model.entity.ArmoryCommandEntity;
import com.zhl.domain.agent.model.valobj.AiAgentEnumVO;
import com.zhl.domain.agent.model.valobj.AiClientApiVO;
import com.zhl.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 *
 * @author zhl
 * @since 2026-04-29 21:52
 */
@Slf4j
@Service
public class AiClientApiNode extends AbstractArmorySupport{


    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 构建，API 构建节点 {}", JSON.toJSONString(requestParameter));
        List<AiClientApiVO> aiClientApiVOList = dynamicContext.getValue(AiAgentEnumVO.AI_CLIENT_API.getDataName());

        if (aiClientApiVOList == null || aiClientApiVOList.isEmpty()) {
            log.warn("没有需要被初始化的 ai client api");
            return null;
        }

        for (AiClientApiVO aiClientApiVO : aiClientApiVOList) {
            // 构建 OpenAiApi
            OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(aiClientApiVO.getBaseUrl())
                .apiKey(aiClientApiVO.getApiKey())
                .embeddingsPath(aiClientApiVO.getEmbeddingsPath())
                .completionsPath(aiClientApiVO.getCompletionsPath())
                .build();

            // 注册 OpenAiApi Bean 对象
            registerBean(AiAgentEnumVO.AI_CLIENT_API.getBeanName(aiClientApiVO.getApiId()), OpenAiApi.class, openAiApi );

        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }
}
