package com.zhl.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI Agent 列表查询请求 DTO
 *
 * @author zhl
 * @description AI Agent查询请求数据传输对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiAgentQueryRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 智能体ID
     */
    private String agentId;

    /**
     * 配置名称（模糊查询）
     */
    private String configName;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;

    /**
     * 页码（从1开始）
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

}
