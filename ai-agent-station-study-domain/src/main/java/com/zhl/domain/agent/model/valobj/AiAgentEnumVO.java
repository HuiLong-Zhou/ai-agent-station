package com.zhl.domain.agent.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>
 *   Agent 通用枚举
 * @author zhl
 * @since 2026-04-08 11:35
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AiAgentEnumVO {

    AI_CLIENT("客户端", "client", "ai_client_", "aiClientLoadDataStrategy"),
    AI_CLIENT_MODEL("对话模型", "model", "ai_client_model_", "aiClientModelLoadDataStrategy"),

    ;

    /**
     * 名称
     */
    private String name;

    /**
     * code
     */
    private String code;

    /**
     * Bean 对象名称标签
     */
    private String beanNameTag;

    /**
     * 装配数据策略
     */
    private String loadDataStrategy;


}

