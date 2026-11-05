package com.zhl.domain.agent.adapter.security;

/**
 * MCP Gateway 访问令牌校验器。
 */
public interface IMcpTokenVerifier {

    /**
     * 校验访问令牌是否有效，并确认其绑定的 MCP 与目标一致。
     *
     * @param token          Bearer Token
     * @param requestedMcpId 请求访问的 MCP 服务 ID
     * @return 校验通过返回 true
     */
    boolean verify(String token, String requestedMcpId);
}
