# MCP Security Minimal Closure

This implementation is disabled by default. Enable it only after applying the
database migration and setting a secret.

## Enable

1. Run `docs/dev-ops/mysql/sql/mcp-security-minimal.sql`.
2. Set `MCP_JWT_SECRET` in the runtime environment. Never commit the value.
3. Configure the protected MCP ids and allowed hosts in `application-dev.yml`.
4. Insert one or more rows into `agent_mcp_tool_permission`.
5. Set `huilian.mcp.security.enabled` to `true`.

Example:

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

Example permission:

```sql
INSERT INTO agent_mcp_tool_permission
  (agent_id, mcp_id, tool_name, permission)
VALUES
  ('agent-inspection', 'prometheus', 'query_metrics', 'ALLOW'),
  ('agent-inspection', 'shell', '*', 'DENY');
```

Rules are evaluated from the most specific `agent_id + mcp_id + tool_name`
to wildcard rules. When security is enabled, an unknown tool is denied.

`APPROVAL` is fail-closed in this minimal version. It is treated as denied
until an approval workflow is implemented.

## What is implemented

- Protected SSE MCP clients receive a short-lived HMAC JWT in the
  `Authorization` header.
- Protected MCP gateways verify issuer, audience, signature, expiration and
  `mcp_id` through `/internal/mcp/auth`.
- Protected SSE endpoints must use HTTPS and can be restricted to an allowed
  host list.
- Protected stdio MCP clients can be restricted to a command allowlist.
- Every tool invocation is checked against `agent_mcp_tool_permission`.
- Success, denied and failed invocations are written to
  `mcp_invocation_audit`; request parameters are stored only as SHA-256 hashes.
- Nginx TLS, rate limiting, SSE proxy settings and `auth_request` sample is in
  `docs/dev-ops/nginx/mcp-security.conf`.

## Current limitations

- The JWT is issued when the MCP client is assembled. A long-running client
  must be reassembled after token expiry.
- Nginx is a sample configuration and is not automatically deployed.
- The approval workflow is not implemented; `APPROVAL` is denied.
- Audit write failures do not block the tool call, but they are logged as
  errors.
- A dedicated MCP Gateway service and Redis-based token revocation are future
  work.
