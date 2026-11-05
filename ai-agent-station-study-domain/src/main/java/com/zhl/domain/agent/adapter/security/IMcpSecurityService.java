package com.zhl.domain.agent.adapter.security;

/**
 * MCP 出站连接安全策略。
 */
public interface IMcpSecurityService {

    /**
     * 为受保护的 MCP 签发访问令牌。
     *
     * @param agentId Agent ID
     * @param mcpId   MCP 服务 ID
     * @param baseUri MCP 基础地址
     * @return Bearer Token；无需鉴权时返回 null
     */
    String issueAccessToken(String agentId, String mcpId, String baseUri);

    /**
     * 校验 SSE MCP 地址的协议和主机白名单。
     *
     * @param mcpId   MCP 服务 ID
     * @param baseUri MCP 基础地址
     */
    void validateSseEndpoint(String mcpId, String baseUri);

    /**
     * 校验 stdio MCP 启动命令是否在允许范围内。
     *
     * @param mcpId   MCP 服务 ID
     * @param command 启动命令
     */
    void validateStdioCommand(String mcpId, String command);
}
