package com.zhl.domain.agent.service.armory;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson2.JSON;
import com.zhl.domain.agent.model.entity.ArmoryCommandEntity;
import com.zhl.domain.agent.model.valobj.enums.AiAgentEnumVO;
import com.zhl.domain.agent.model.valobj.enums.AiClientAdvisorTypeEnumVO;
import com.zhl.domain.agent.model.valobj.AiClientAdvisorVO;
import com.zhl.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  顾问角色节点
 * @author zhl
 * @since 2026-05-02 22:20
 */
@Slf4j
@Service
public class AiClientAdvisorNode extends AbstractArmorySupport {

    @Resource
    private AiClientNode aiClientNode;

    @Resource
    private VectorStore vectorStore;


    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 构建，advisor {}", JSON.toJSONString(requestParameter));
        List<AiClientAdvisorVO> aiClientAdvisorVOList = dynamicContext.getValue(dataName());

        if(aiClientAdvisorVOList == null || aiClientAdvisorVOList.isEmpty()) {
            log.warn("没有需要被初始化的 ai client advisor");
            return router(requestParameter, dynamicContext);
        }

        for (AiClientAdvisorVO aiClientAdvisorVO : aiClientAdvisorVOList) {

            // 构建 Advisor 对象
            Advisor advisor = createAdvisor(aiClientAdvisorVO);

            // 注册 Advisor Bean对象
            registerBean(beanName(aiClientAdvisorVO.getAdvisorId()), Advisor.class, advisor);
        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientNode;
    }

    private Advisor createAdvisor(AiClientAdvisorVO aiClientAdvisorVO) {
        String advisorType = aiClientAdvisorVO.getAdvisorType();
        AiClientAdvisorTypeEnumVO enumVO = AiClientAdvisorTypeEnumVO.getByCode(advisorType);
        return enumVO.createAdvisor(aiClientAdvisorVO, vectorStore);
    }

    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT_ADVISOR.getBeanName(beanId);
    }

    @Override
    protected String dataName() {
        return AiAgentEnumVO.AI_CLIENT_ADVISOR.getDataName();
    }
}
