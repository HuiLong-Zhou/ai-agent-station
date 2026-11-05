package com.zhl.domain.agent.service.security;

import com.zhl.domain.agent.adapter.security.IMcpToolGuard;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

import java.util.UUID;

/**
 * 对每次 MCP 工具调用应用授权校验和审计记录。
 */
public class SecureToolCallback implements ToolCallback {

    /**
     * 原始的 MCP 工具回调。
     */
    private final ToolCallback delegate;

    /**
     * 当前工具所属的 Agent ID。
     */
    private final String agentId;

    /**
     * 当前工具所属的 MCP 服务 ID。
     */
    private final String mcpId;

    /**
     * 工具权限与审计守卫。
     */
    private final IMcpToolGuard toolGuard;

    /**
     * 创建安全工具回调。
     *
     * @param delegate  原始工具回调
     * @param agentId   Agent ID
     * @param mcpId     MCP 服务 ID
     * @param toolGuard 工具权限与审计守卫
     */
    public SecureToolCallback(ToolCallback delegate, String agentId,
                              String mcpId, IMcpToolGuard toolGuard) {
        this.delegate = delegate;
        this.agentId = agentId;
        this.mcpId = mcpId;
        this.toolGuard = toolGuard;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return delegate.getToolDefinition();
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return delegate.getToolMetadata();
    }

    @Override
    public String call(String toolInput) {
        return invoke(toolInput, null);
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        return invoke(toolInput, toolContext);
    }

    /**
     * 执行权限校验、真实工具调用和结果审计。
     *
     * @param toolInput   工具入参
     * @param toolContext Spring AI 工具上下文
     * @return 工具执行结果
     */
    private String invoke(String toolInput, ToolContext toolContext) {
        String traceId = UUID.randomUUID().toString();
        String toolName = delegate.getToolDefinition().name();
        long start = System.currentTimeMillis();

        try {
            toolGuard.checkAllowed(traceId, agentId, mcpId, toolName);
            String result = toolContext == null
                ? delegate.call(toolInput)
                : delegate.call(toolInput, toolContext);
            toolGuard.recordSuccess(traceId, agentId, mcpId, toolName,
                toolInput, System.currentTimeMillis() - start);
            return result;
        } catch (RuntimeException e) {
            toolGuard.recordFailure(traceId, agentId, mcpId, toolName, toolInput, e);
            throw e;
        }
    }
}
