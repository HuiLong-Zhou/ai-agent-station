package com.zhl.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent 对指定 MCP 工具的权限配置。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpToolPermission {

    /**
     * 主键 ID。
     */
    private Long id;

    /**
     * Agent ID，支持通配符。
     */
    private String agentId;

    /**
     * MCP 服务 ID，支持通配符。
     */
    private String mcpId;

    /**
     * 工具名称，支持通配符。
     */
    private String toolName;

    /**
     * 权限类型：ALLOW、DENY 或 APPROVAL。
     */
    private String permission;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    private LocalDateTime updateTime;
}
