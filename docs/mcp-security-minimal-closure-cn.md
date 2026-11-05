# MCP 最小安全闭环

本实现默认关闭。只有在执行数据库迁移并设置密钥之后，才应启用。

## 启用方式

1. 执行 `docs/dev-ops/mysql/sql/mcp-security-minimal.sql`。
2. 在运行环境中设置 `MCP_JWT_SECRET`，不要将密钥提交到代码仓库。
3. 在 `application-dev.yml` 中配置需要保护的 MCP ID 和允许访问的主机。
4. 向 `agent_mcp_tool_permission` 表插入一条或多条工具权限记录。
5. 将 `huilian.mcp.security.enabled` 设置为 `true`。

配置示例：

```yaml
huilian:
  mcp:
    security:
      enabled: true
      issuer: huilian-agent
      audience: mcp-gateway
      secret: ${MCP_JWT_SECRET:}
      token-ttl-seconds: 3600
      protected-mcp-ids:
        - prometheus
        - elk
      allowed-hosts:
        - mcp.example.com
      allowed-stdio-commands:
        - node
```

工具权限示例：

```sql
INSERT INTO agent_mcp_tool_permission
  (agent_id, mcp_id, tool_name, permission)
VALUES
  ('agent-inspection', 'prometheus', 'query_metrics', 'ALLOW'),
  ('agent-inspection', 'shell', '*', 'DENY');
```

权限匹配按照从具体的 `agent_id + mcp_id + tool_name` 规则到通配规则的顺序执行。
开启安全后，未配置权限的工具默认拒绝。

在当前最小版本中，`APPROVAL` 采用失败关闭策略。在审批流程实现之前，该权限
等同于拒绝。

## 已实现功能

- 受保护的 SSE MCP 客户端通过 `Authorization` 请求头携带短期 HMAC JWT。
- 受保护的 MCP Gateway 通过 `/internal/mcp/auth` 校验 issuer、audience、 签名、过期时间和 `mcp_id`。
- 受保护的 SSE 端点必须使用 HTTPS，并可通过主机白名单限制访问目标。
- 受保护的 stdio MCP 客户端可通过命令白名单限制可执行程序。
- 每次工具调用都会根据 `agent_mcp_tool_permission` 进行权限检查。
- 成功、拒绝和失败的工具调用都会写入 `mcp_invocation_audit`；请求参数仅以SHA-256 摘要形式存储。
- Nginx 的 TLS、限流、SSE 代理设置和 `auth_request` 示例位于 `docs/dev-ops/nginx/mcp-security.conf`。

## 当前限制

- JWT 在 MCP 客户端装配时签发。长期运行的客户端需要在 Token 过期后重新装配。
- Nginx 配置仅为示例，不会自动部署。
- 审批流程尚未实现，`APPROVAL` 会被拒绝。
- 审计写入失败不会阻塞工具调用，但会记录错误日志。
- 独立的 MCP Gateway 服务和基于 Redis 的 Token 吊销属于后续工作。
