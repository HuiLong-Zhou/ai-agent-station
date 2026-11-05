package com.zhl.infrastructure.adapter.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.zhl.domain.agent.adapter.security.IMcpSecurityService;
import com.zhl.domain.agent.adapter.security.IMcpTokenVerifier;
import com.zhl.types.exception.McpSecurityException;
import com.zhl.types.security.McpSecurityProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * 负责签发和校验 MCP Gateway 使用的短期 HMAC JWT。
 */
@Slf4j
@Service
public class McpJwtSecurityService implements IMcpSecurityService, IMcpTokenVerifier {

    /**
     * MCP 安全配置。
     */
    @Resource
    private McpSecurityProperties properties;

    /**
     * 为受保护的 MCP 签发短期访问令牌。
     *
     * @param agentId Agent ID
     * @param mcpId   MCP 服务 ID
     * @param baseUri MCP 基础地址
     * @return JWT；无需鉴权时返回 null
     */
    @Override
    public String issueAccessToken(String agentId, String mcpId, String baseUri) {
        if (!properties.isEnabled() || !isProtectedMcp(mcpId)) {
            return null;
        }
        requireSecret();

        Instant now = Instant.now();
        return JWT.create()
            .withIssuer(properties.getIssuer())
            .withAudience(properties.getAudience())
            .withSubject(StringUtils.hasText(agentId) ? agentId : "unknown-agent")
            .withClaim("mcp_id", mcpId)
            .withClaim("scope", "mcp:tool:invoke")
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusSeconds(properties.getTokenTtlSeconds())))
            .withJWTId(UUID.randomUUID().toString())
            .sign(Algorithm.HMAC256(properties.getSecret()));
    }

    /**
     * 校验受保护 SSE MCP 必须使用 HTTPS，并检查主机白名单。
     *
     * @param mcpId   MCP 服务 ID
     * @param baseUri MCP 基础地址
     */
    @Override
    public void validateSseEndpoint(String mcpId, String baseUri) {
        if (!properties.isEnabled() || !isProtectedMcp(mcpId)) {
            return;
        }

        URI uri;
        try {
            uri = URI.create(baseUri);
        } catch (IllegalArgumentException e) {
            throw new McpSecurityException("MCP_ENDPOINT_INVALID", "MCP endpoint is invalid");
        }
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new McpSecurityException("MCP_TLS_REQUIRED",
                "Protected MCP endpoints must use HTTPS");
        }
        if (!properties.getAllowedHosts().isEmpty()
            && !properties.getAllowedHosts().contains(uri.getHost())) {
            throw new McpSecurityException("MCP_HOST_DENIED",
                "MCP host is not in the allowlist: " + uri.getHost());
        }
    }

    /**
     * 校验受保护 stdio MCP 的启动命令是否在白名单中。
     *
     * @param mcpId   MCP 服务 ID
     * @param command 启动命令
     */
    @Override
    public void validateStdioCommand(String mcpId, String command) {
        if (!properties.isEnabled() || !isProtectedMcp(mcpId)) {
            return;
        }
        if (properties.getAllowedStdioCommands().isEmpty()) {
            log.warn("MCP security enabled but allowed-stdio-commands is empty, mcpId={}", mcpId);
            return;
        }
        if (!properties.getAllowedStdioCommands().contains(command)) {
            throw new McpSecurityException("MCP_STDIO_COMMAND_DENIED",
                "stdio command is not in the allowlist: " + command);
        }
    }

    /**
     * 校验 JWT 的签名、签发方、接收方、有效期和 MCP 绑定关系。
     *
     * @param token          Bearer Token
     * @param requestedMcpId 请求访问的 MCP 服务 ID
     * @return 校验通过返回 true
     */
    @Override
    public boolean verify(String token, String requestedMcpId) {
        if (!properties.isEnabled()) {
            return true;
        }
        if (!StringUtils.hasText(requestedMcpId)) {
            return false;
        }
        if (!isProtectedMcp(requestedMcpId)) {
            return true;
        }
        if (!StringUtils.hasText(token) || !StringUtils.hasText(properties.getSecret())) {
            return false;
        }

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(properties.getSecret()))
                .withIssuer(properties.getIssuer())
                .withAudience(properties.getAudience())
                .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String tokenMcpId = decodedJWT.getClaim("mcp_id").asString();
            return requestedMcpId.equals(tokenMcpId);
        } catch (RuntimeException e) {
            log.warn("MCP JWT verification failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断指定 MCP 是否启用访问令牌保护。
     *
     * @param mcpId MCP 服务 ID
     * @return 受保护返回 true
     */
    private boolean isProtectedMcp(String mcpId) {
        return mcpId != null && properties.getProtectedMcpIds().contains(mcpId);
    }

    /**
     * 确保启用安全校验时已经配置签名密钥。
     */
    private void requireSecret() {
        if (!StringUtils.hasText(properties.getSecret())) {
            throw new McpSecurityException("MCP_JWT_SECRET_MISSING",
                "MCP_JWT_SECRET must be configured when MCP security is enabled");
        }
    }
}
