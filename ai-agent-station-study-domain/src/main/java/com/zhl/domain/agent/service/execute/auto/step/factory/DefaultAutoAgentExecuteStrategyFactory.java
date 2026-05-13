package com.zhl.domain.agent.service.execute.auto.step.factory;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.zhl.domain.agent.model.entity.ExecuteCommandEntity;
import com.zhl.domain.agent.model.valobj.AiAgentClientFlowConfigVO;
import com.zhl.domain.agent.service.execute.auto.step.RootNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  工厂类
 * @author zhl
 * @since 2026-05-12 15:16
 */
@Service
public class DefaultAutoAgentExecuteStrategyFactory {
    private final RootNode executeRootNode;

    public DefaultAutoAgentExecuteStrategyFactory(RootNode executeRootNode) {
        this.executeRootNode = executeRootNode;
    }

    public StrategyHandler<ExecuteCommandEntity, DynamicContext, String> armoryStrategyHandler(){
        return executeRootNode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext {

        // 任务执行步骤
        private int step = 1;

        // 最大任务步骤
        private int maxStep = 1;

        private StringBuilder executionHistory;

        private String currentTask;

        boolean isCompleted = false;

        private Map<String, AiAgentClientFlowConfigVO> aiAgentClientFlowConfigVOMap;

        private Map<String, Object> dataObjects = new HashMap<>();

        public <T> void setValue(String key, T value) {
            dataObjects.put(key, value);
        }

        public <T> T getValue(String key) {
            return (T) dataObjects.get(key);
        }
    }


}
