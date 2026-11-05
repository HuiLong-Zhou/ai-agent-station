-- MCP 安全最小闭环：
-- 1. 工具级权限控制。
-- 2. MCP 工具调用审计。
--
-- 鉴权默认关闭。只有在配置 MCP_JWT_SECRET 和受保护 MCP ID 后才应启用。

DROP TABLE IF EXISTS `agent_mcp_tool_permission`;

CREATE TABLE `agent_mcp_tool_permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `agent_id` varchar(64) NOT NULL COMMENT 'Agent ID（支持通配符*）',
    `mcp_id` varchar(64) NOT NULL COMMENT 'MCP服务ID（支持通配符*）',
    `tool_name` varchar(128) NOT NULL COMMENT 'MCP工具名称（支持通配符*）',
    `permission` varchar(16) NOT NULL COMMENT '权限类型(ALLOW:允许,DENY:拒绝,APPROVAL:需审批)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_agent_mcp_tool` (`agent_id`, `mcp_id`, `tool_name`),
    KEY `idx_mcp_tool` (`mcp_id`, `tool_name`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Agent MCP工具权限表';

DROP TABLE IF EXISTS `mcp_invocation_audit`;

CREATE TABLE `mcp_invocation_audit` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `trace_id` varchar(64) NOT NULL COMMENT 'MCP调用链路追踪ID',
    `agent_id` varchar(64) NOT NULL COMMENT '发起调用的Agent ID',
    `mcp_id` varchar(64) NOT NULL COMMENT 'MCP服务ID',
    `tool_name` varchar(128) NOT NULL COMMENT 'MCP工具名称',
    `request_hash` varchar(64) DEFAULT NULL COMMENT '请求参数SHA-256摘要',
    `status` varchar(16) NOT NULL COMMENT '调用状态(SUCCESS:成功,DENIED:拒绝,ERROR:失败)',
    `latency_ms` bigint DEFAULT NULL COMMENT '调用耗时（毫秒）',
    `error_message` varchar(1000) DEFAULT NULL COMMENT '错误信息',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_trace_id` (`trace_id`),
    KEY `idx_agent_time` (`agent_id`, `create_time`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='MCP工具调用审计表';

-- Example tool permissions. Replace ids/tool names with real MCP metadata.
-- INSERT INTO agent_mcp_tool_permission
--   (agent_id, mcp_id, tool_name, permission)
-- VALUES
--   ('agent-inspection', 'prometheus', 'query_metrics', 'ALLOW'),
--   ('agent-inspection', 'shell', '*', 'DENY');
