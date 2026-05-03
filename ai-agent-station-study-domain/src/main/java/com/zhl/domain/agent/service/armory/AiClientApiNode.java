package com.zhl.domain.agent.service.armory;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson2.JSON;
import com.zhl.domain.agent.model.entity.ArmoryCommandEntity;
import com.zhl.domain.agent.model.valobj.AiAgentEnumVO;
import com.zhl.domain.agent.model.valobj.AiClientApiVO;
import com.zhl.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 *  OpenAI API配置节点
 * @author zhl
 * @since 2026-04-29 21:52
 */
@Slf4j
@Service
public class AiClientApiNode extends AbstractArmorySupport{


    @Resource
    private AiClientToolMcpNode aiClientToolMcpNode;


    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 构建，API 构建节点 {}", JSON.toJSONString(requestParameter));
        List<AiClientApiVO> aiClientApiVOList = dynamicContext.getValue(dataName());

        if (aiClientApiVOList == null || aiClientApiVOList.isEmpty()) {
            log.warn("没有需要被初始化的 ai client api");
            // 返回下一个需要待处理的 node
            return router(requestParameter, dynamicContext);
        }

        for (AiClientApiVO aiClientApiVO : aiClientApiVOList) {
            // 构建 OpenAiApi
            OpenAiApi openAiApi = createOpenAiApi(aiClientApiVO);

            // 注册 OpenAiApi Bean 对象
            registerBean(beanName(aiClientApiVO.getApiId()), OpenAiApi.class, openAiApi );
        }

        return router(requestParameter, dynamicContext);
    }

    private static OpenAiApi createOpenAiApi(AiClientApiVO aiClientApiVO) {
        return OpenAiApi.builder()
            .baseUrl(aiClientApiVO.getBaseUrl())
            .apiKey(aiClientApiVO.getApiKey())
            .embeddingsPath(aiClientApiVO.getEmbeddingsPath())
            .completionsPath(aiClientApiVO.getCompletionsPath()).build();
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientToolMcpNode;
    }


    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT_API.getBeanName(beanId);
    }

    @Override
    protected String dataName() {
        return AiAgentEnumVO.AI_CLIENT_API.getDataName();
    }

}
