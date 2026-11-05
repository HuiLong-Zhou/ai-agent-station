package com.zhl.trigger.http.security;

import com.zhl.domain.agent.adapter.security.IMcpTokenVerifier;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 供 Nginx auth_request 或 MCP Gateway 调用的内部认证端点。
 */
@RestController
@RequestMapping("/internal/mcp")
public class McpGatewayAuthController {

    /**
     * MCP 访问令牌校验器。
     */
    @Resource
    private IMcpTokenVerifier tokenVerifier;

    /**
     * 校验请求携带的 JWT 是否允许访问指定 MCP。
     *
     * @param authorization Authorization 请求头
     * @param mcpId         X-MCP-Id 请求头
     * @return 校验通过返回 204，否则返回 401
     */
    @GetMapping("/auth")
    public ResponseEntity<Void> authenticate(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestHeader(value = "X-MCP-Id", required = false) String mcpId) {

        String token = bearerToken(authorization);
        if (tokenVerifier.verify(token, mcpId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 从 Authorization 请求头中提取 Bearer Token。
     *
     * @param authorization Authorization 请求头
     * @return Token；格式不合法时返回 null
     */
    private String bearerToken(String authorization) {
        if (authorization == null
            || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return null;
        }
        return authorization.substring(7).trim();
    }
}
