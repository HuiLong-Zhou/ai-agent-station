package com.zhl.infrastructure.adapter.security;

import com.zhl.domain.agent.adapter.security.IMcpToolGuard;
import com.zhl.infrastructure.dao.IMcpInvocationAuditDao;
import com.zhl.infrastructure.dao.IMcpToolPermissionDao;
import com.zhl.infrastructure.dao.po.McpInvocationAudit;
import com.zhl.infrastructure.dao.po.McpToolPermission;
import com.zhl.types.exception.McpSecurityException;
import com.zhl.types.security.McpSecurityProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

/**
 * 执行 MCP 工具权限校验，并写入脱敏后的调用审计记录。
 */
@Slf4j
@Service
public class McpToolGuardService implements IMcpToolGuard {

    /**
     * MCP 安全配置。
     */
    @Resource
    private McpSecurityProperties properties;

    /**
     * 工具权限数据访问对象。
     */
    @Resource
    private IMcpToolPermissionDao toolPermissionDao;

    /**
     * 调用审计数据访问对象。
     */
    @Resource
    private IMcpInvocationAuditDao invocationAuditDao;

    /**
     * 校验当前 Agent 是否允许调用指定工具。
     * 开启安全后，未配置权限和 APPROVAL 状态均按拒绝处理。
     *
     * @param traceId  调用链路 ID
     * @param agentId  Agent ID
     * @param mcpId    MCP 服务 ID
     * @param toolName 工具名称
     */
    @Override
    public void checkAllowed(String traceId, String agentId, String mcpId, String toolName) {
        if (!properties.isEnabled()) {
            return;
        }

        McpToolPermission permission =
            toolPermissionDao.queryPermission(agentId, mcpId, toolName);
        if (permission == null) {
            throw new McpSecurityException("MCP_TOOL_DENIED",
                "No permission configured for tool " + mcpId + ":" + toolName);
        }

        String level = permission.getPermission() == null
            ? "" : permission.getPermission().toUpperCase(Locale.ROOT);
        switch (level) {
            case "ALLOW" -> {
                return;
            }
            case "APPROVAL" -> throw new McpSecurityException("MCP_TOOL_APPROVAL_REQUIRED",
                "Tool requires approval " + mcpId + ":" + toolName);
            default -> throw new McpSecurityException("MCP_TOOL_DENIED",
                "Tool is denied " + mcpId + ":" + toolName);
        }
    }

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
    @Override
    public void recordSuccess(String traceId, String agentId, String mcpId,
                              String toolName, String toolInput, long latencyMs) {
        record(traceId, agentId, mcpId, toolName, toolInput,
            "SUCCESS", latencyMs, null);
    }

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
    @Override
    public void recordFailure(String traceId, String agentId, String mcpId,
                              String toolName, String toolInput, Throwable error) {
        String status = error instanceof McpSecurityException ? "DENIED" : "ERROR";
        record(traceId, agentId, mcpId, toolName, toolInput, status, null, error);
    }

    /**
     * 写入审计记录。审计失败只记录日志，不中断已获授权的工具调用。
     */
    private void record(String traceId, String agentId, String mcpId, String toolName,
                        String toolInput, String status, Long latencyMs, Throwable error) {
        if (!properties.isEnabled()) {
            return;
        }

        try {
            invocationAuditDao.insert(McpInvocationAudit.builder()
                .traceId(traceId)
                .agentId(agentId)
                .mcpId(mcpId)
                .toolName(toolName)
                .requestHash(sha256(toolInput))
                .status(status)
                .latencyMs(latencyMs)
                .errorMessage(error == null ? null : truncate(error.getMessage(), 1000))
                .build());
        } catch (RuntimeException e) {
            log.error("Failed to persist MCP audit record, traceId={}", traceId, e);
        }
    }

    /**
     * 计算工具入参的 SHA-256 摘要，避免审计表保存敏感原文。
     *
     * @param value 原始入参
     * @return 十六进制摘要
     */
    private String sha256(String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(
                digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    /**
     * 截断过长的错误信息，避免超过审计字段长度。
     *
     * @param value     原始文本
     * @param maxLength 最大保留长度
     * @return 截断后的文本
     */
    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
