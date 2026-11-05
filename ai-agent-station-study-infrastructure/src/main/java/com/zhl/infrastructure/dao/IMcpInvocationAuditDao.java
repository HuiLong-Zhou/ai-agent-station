package com.zhl.infrastructure.dao;

import com.zhl.infrastructure.dao.po.McpInvocationAudit;
import org.apache.ibatis.annotations.Mapper;

/**
 * MCP 工具调用审计 DAO。
 */
@Mapper
public interface IMcpInvocationAuditDao {

    /**
     * 新增一条 MCP 工具调用审计记录。
     *
     * @param audit 审计记录
     * @return 影响行数
     */
    int insert(McpInvocationAudit audit);
}
