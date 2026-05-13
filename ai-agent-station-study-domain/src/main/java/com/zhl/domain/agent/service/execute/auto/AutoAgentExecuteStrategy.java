package com.zhl.domain.agent.service.execute.auto;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.zhl.domain.agent.model.entity.ExecuteCommandEntity;
import com.zhl.domain.agent.service.execute.IExecuteStrategy;
import com.zhl.domain.agent.service.execute.auto.step.factory.DefaultAutoAgentExecuteStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  自动执行策略
 * @author zhl
 * @since 2026-05-12 15:15
 */
@Slf4j
@Service
public class AutoAgentExecuteStrategy implements IExecuteStrategy {
    @Resource
    private DefaultAutoAgentExecuteStrategyFactory defaultAutoAgentExecuteStrategyFactory;

    @Override
    public void execute(ExecuteCommandEntity executeCommandEntity) throws Exception {
        StrategyHandler<ExecuteCommandEntity, DefaultAutoAgentExecuteStrategyFactory.DynamicContext, String> executeHandler
            = defaultAutoAgentExecuteStrategyFactory.armoryStrategyHandler();
        String apply = executeHandler.apply(executeCommandEntity, new DefaultAutoAgentExecuteStrategyFactory.DynamicContext());
        log.info("测试结果:{}", apply);
    }

}
