package com.zhl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * <p>
 *  AI Agent 自动装配配置属性
 * @author zhl
 * @since 2026-05-13 15:18
 */
@Data
@ConfigurationProperties(prefix = "spring.ai.agent.auto-config")
public class AiAgentAutoConfigProperties {

    /**
     * 是否启用AI Agent自动装配
     */
    private boolean enabled = false;

    /**
     * 需要自动装配的客户端ID列表
     */
    private List<String> clientIds;

}
