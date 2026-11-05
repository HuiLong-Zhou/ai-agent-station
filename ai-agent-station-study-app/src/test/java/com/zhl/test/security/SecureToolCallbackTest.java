package com.zhl.test.security;

import com.zhl.domain.agent.adapter.security.IMcpToolGuard;
import com.zhl.domain.agent.service.security.SecureToolCallback;
import com.zhl.types.exception.McpSecurityException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 安全工具回调的权限校验与审计测试。
 */
class SecureToolCallbackTest {

    /**
     * 验证工具调用成功后会记录成功审计，不记录失败审计。
     */
    @Test
    void shouldAuditSuccessfulInvocation() {
        ToolCallback delegate = new StubToolCallback();
        IMcpToolGuard guard = mock(IMcpToolGuard.class);
        SecureToolCallback callback =
            new SecureToolCallback(delegate, "agent-a", "prometheus", guard);

        assertEquals("ok", callback.call("{\"query\":\"up\"}"));
        verify(guard).checkAllowed(anyString(), eq("agent-a"),
            eq("prometheus"), eq("query_metrics"));
        verify(guard).recordSuccess(anyString(), eq("agent-a"),
            eq("prometheus"), eq("query_metrics"), anyString(), anyLong());
        verify(guard, never()).recordFailure(
            anyString(), anyString(), anyString(), anyString(), anyString(), any());
    }

    /**
     * 验证权限拒绝后不会执行真实工具，并记录失败审计。
     */
    @Test
    void shouldAuditDeniedInvocation() {
        ToolCallback delegate = new StubToolCallback();
        IMcpToolGuard guard = mock(IMcpToolGuard.class);
        doThrow(new McpSecurityException("MCP_TOOL_DENIED", "denied"))
            .when(guard).checkAllowed(anyString(), anyString(), anyString(), anyString());
        SecureToolCallback callback =
            new SecureToolCallback(delegate, "agent-a", "prometheus", guard);

        assertThrows(McpSecurityException.class, () -> callback.call("{}"));
        verify(guard).recordFailure(anyString(), eq("agent-a"),
            eq("prometheus"), eq("query_metrics"), eq("{}"), any());
    }

    /**
     * 用于测试的简单 MCP 工具回调。
     */
    private static class StubToolCallback implements ToolCallback {

        @Override
        public ToolDefinition getToolDefinition() {
            return ToolDefinition.builder()
                .name("query_metrics")
                .description("query metrics")
                .inputSchema("{}")
                .build();
        }

        @Override
        public String call(String toolInput) {
            return "ok";
        }
    }
}
