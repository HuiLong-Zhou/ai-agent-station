package com.zhl.domain.agent.adapter.security;

/**
 * MCP 工具级授权与审计边界。
 */
public interface IMcpToolGuard {

    /**
     * 校验当前 Agent 是否有权调用指定 MCP 工具。
     *
     * @param traceId  调用链路 ID
     * @param agentId  Agent ID
     * @param mcpId    MCP 服务 ID
     * @param toolName 工具名称
     */
    void checkAllowed(String traceId, String agentId, String mcpId, String toolName);

    /**
     * 记录工具调用成功审计。
     *
     * @param traceId   调用链路 ID
     * @param agentId   Agent ID
     * @param mcpId     MCP 服务 ID
     * @param toolName  工具名称
     * @param toolInput 工具入参
     * @param latencyMs 调用耗时
     */
    void recordSuccess(String traceId, String agentId, String mcpId,
                       String toolName, String toolInput, long latencyMs);

    /**
     * 记录工具调用失败或被拒绝的审计。
     *
     * @param traceId   调用链路 ID
     * @param agentId   Agent ID
     * @param mcpId     MCP 服务 ID
     * @param toolName  工具名称
     * @param toolInput 工具入参
     * @param error     失败原因
     */
    void recordFailure(String traceId, String agentId, String mcpId,
                       String toolName, String toolInput, Throwable error);
}
