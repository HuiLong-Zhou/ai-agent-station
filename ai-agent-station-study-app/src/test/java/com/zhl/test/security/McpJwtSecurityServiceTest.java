package com.zhl.test.security;

import com.zhl.infrastructure.adapter.security.McpJwtSecurityService;
import com.zhl.types.exception.McpSecurityException;
import com.zhl.types.security.McpSecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MCP JWT 签发、验签和端点安全策略测试。
 */
class McpJwtSecurityServiceTest {

    /**
     * 被测 JWT 安全服务。
     */
    private McpJwtSecurityService service;

    /**
     * 初始化测试配置和被测服务。
     */
    @BeforeEach
    void setUp() {
        McpSecurityProperties properties = new McpSecurityProperties();
        properties.setEnabled(true);
        properties.setSecret("test-secret-at-least-32-bytes-long");
        properties.setIssuer("huilian-agent");
        properties.setAudience("mcp-gateway");
        properties.setProtectedMcpIds(Set.of("prometheus", "elk"));
        properties.setAllowedHosts(Set.of("mcp.example.com"));

        service = new McpJwtSecurityService();
        ReflectionTestUtils.setField(service, "properties", properties);
    }

    /**
     * 验证受保护 MCP 可以签发 Token，并且 Token 不能用于其他受保护 MCP。
     */
    @Test
    void shouldIssueAndVerifyTokenForProtectedMcp() {
        String token = service.issueAccessToken(
            "agent-a", "prometheus", "https://mcp.example.com");

        assertNotNull(token);
        assertTrue(service.verify(token, "prometheus"));
        assertFalse(service.verify(token, "elk"));
        assertFalse(service.verify(token, null));
    }

    /**
     * 验证受保护 SSE MCP 会拒绝 HTTP 和非白名单主机。
     */
    @Test
    void shouldRejectNonTlsAndUnknownHost() {
        assertThrows(McpSecurityException.class,
            () -> service.validateSseEndpoint(
                "prometheus", "http://mcp.example.com"));
        assertThrows(McpSecurityException.class,
            () -> service.validateSseEndpoint(
                "prometheus", "https://unknown.example.com"));
    }

    /**
     * 验证未受保护的 MCP 不签发访问令牌。
     */
    @Test
    void shouldNotIssueTokenForUnprotectedMcp() {
        assertNull(service.issueAccessToken(
            "agent-a", "public-search", "https://mcp.example.com"));
    }
}
