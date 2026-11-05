package com.zhl.types.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * MCP 安全最小闭环配置。
 */
@Data
@ConfigurationProperties(prefix = "huilian.mcp.security")
public class McpSecurityProperties {

    /**
     * 是否启用 MCP 安全校验，默认关闭以兼容现有调用链。
     */
    private boolean enabled;

    /**
     * JWT 签发方。
     */
    private String issuer = "huilian-agent";

    /**
     * JWT 接收方，MCP Gateway 验签时需要匹配。
     */
    private String audience = "mcp-gateway";

    /**
     * HMAC 签名密钥，应从环境变量或密钥管理系统注入。
     */
    private String secret;

    /**
     * JWT 有效期，单位为秒。
     */
    private long tokenTtlSeconds = 3600;

    /**
     * 需要携带 Authorization 请求头的 MCP 服务 ID 集合。
     */
    private Set<String> protectedMcpIds = new HashSet<>();

    /**
     * 允许访问的 MCP 主机白名单；为空时不限制主机。
     */
    private Set<String> allowedHosts = new HashSet<>();

    /**
     * 允许启动的 stdio 可执行命令白名单；为空时不限制命令。
     */
    private Set<String> allowedStdioCommands = new HashSet<>();
}
