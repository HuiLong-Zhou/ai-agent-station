package com.zhl.config;


import com.zhl.types.security.McpSecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 启用 MCP 安全配置绑定。
 */
@Configuration
@EnableConfigurationProperties(McpSecurityProperties.class)
public class McpSecurityConfiguration {
}
