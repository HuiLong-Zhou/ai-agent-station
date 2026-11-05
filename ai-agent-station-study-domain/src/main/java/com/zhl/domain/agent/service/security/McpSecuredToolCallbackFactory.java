package com.zhl.domain.agent.service.security;

import com.zhl.domain.agent.adapter.security.IMcpToolGuard;
import io.modelcontextprotocol.client.McpSyncClient;
import jakarta.annotation.Resource;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 将 MCP 工具转换为带权限校验和审计能力的 Spring AI 工具回调。
 */
@Component
public class McpSecuredToolCallbackFactory {

    /**
     * MCP 工具调用守卫。
     */
    @Resource
    private IMcpToolGuard mcpToolGuard;

    /**
     * 按 MCP Client 包装工具回调，确保每个工具都绑定所属 MCP 和 Agent。
     *
     * @param agentId    Agent ID
     * @param mcpIds     MCP 服务 ID 列表
     * @param mcpClients 已初始化的 MCP Client 列表
     * @return 安全包装后的工具回调列表
     */
    public List<ToolCallback> wrap(String agentId, List<String> mcpIds,
                                   List<McpSyncClient> mcpClients) {
        List<ToolCallback> result = new ArrayList<>();
        int size = Math.min(mcpIds.size(), mcpClients.size());

        for (int i = 0; i < size; i++) {
            String mcpId = mcpIds.get(i);
            ToolCallback[] callbacks =
                new SyncMcpToolCallbackProvider(mcpClients.get(i)).getToolCallbacks();
            for (ToolCallback callback : callbacks) {
                result.add(new SecureToolCallback(callback, agentId, mcpId, mcpToolGuard));
            }
        }

        return result;
    }
}
