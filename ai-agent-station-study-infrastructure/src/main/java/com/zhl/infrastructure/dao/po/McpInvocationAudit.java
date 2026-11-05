package com.zhl.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 一次 MCP 工具调用的审计记录。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpInvocationAudit {

    /**
     * 主键 ID。
     */
    private Long id;

    /**
     * 调用链路 ID。
     */
    private String traceId;

    /**
     * Agent ID。
     */
    private String agentId;

    /**
     * MCP 服务 ID。
     */
    private String mcpId;

    /**
     * 工具名称。
     */
    private String toolName;

    /**
     * 工具入参的 SHA-256 摘要。
     */
    private String requestHash;

    /**
     * 调用状态：SUCCESS、DENIED 或 ERROR。
     */
    private String status;

    /**
     * 调用耗时，单位毫秒。
     */
    private Long latencyMs;

    /**
     * 错误信息。
     */
    private String errorMessage;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;
}
