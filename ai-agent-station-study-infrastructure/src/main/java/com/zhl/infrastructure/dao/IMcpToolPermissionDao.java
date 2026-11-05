package com.zhl.infrastructure.dao;

import com.zhl.infrastructure.dao.po.McpToolPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MCP 工具权限 DAO。
 */
@Mapper
public interface IMcpToolPermissionDao {

    /**
     * 按 Agent、MCP 和工具名称查询匹配度最高的权限规则。
     *
     * @param agentId  Agent ID
     * @param mcpId    MCP 服务 ID
     * @param toolName 工具名称
     * @return 权限配置，未找到时返回 null
     */
    McpToolPermission queryPermission(@Param("agentId") String agentId,
                                      @Param("mcpId") String mcpId,
                                      @Param("toolName") String toolName);
}
