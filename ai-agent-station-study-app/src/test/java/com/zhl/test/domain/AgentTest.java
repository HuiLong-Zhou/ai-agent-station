package com.zhl.test.domain;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.zhl.domain.agent.model.entity.ArmoryCommandEntity;
import com.zhl.domain.agent.model.valobj.AiAgentEnumVO;
import com.zhl.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * <p>
 *
 * @author zhl
 * @since 2026-04-30 11:16
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AgentTest {

    @Resource
    private DefaultArmoryStrategyFactory strategyFactory;

    @Resource
    private ApplicationContext applicationContext;

    @Test
    public void testAiAgentApi() throws Exception {
        StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> strategyHandler =
            strategyFactory.strategyHandler();

        String apply = strategyHandler.apply(
            ArmoryCommandEntity.builder().
                commandType(AiAgentEnumVO.AI_CLIENT.getCode())
                .commandIdList(Arrays.asList("3001")).build(),
            new DefaultArmoryStrategyFactory.DynamicContext());

        OpenAiApi openAiApi = (OpenAiApi) applicationContext.getBean(AiAgentEnumVO.AI_CLIENT_API.getBeanName("1001"));

        log.info("测试结果：{}", openAiApi);

    }

}
