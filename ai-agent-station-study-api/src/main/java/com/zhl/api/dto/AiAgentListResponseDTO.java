package com.zhl.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI Agent列表响应 DTO
 *
 * @author zhl
 * @description AI Agent列表配置响应数据传输对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiAgentListResponseDTO  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private String configId;

    /**
     * 智能体ID
     */
    private String agentId;

    /**
     * 配置名称
     */
    private String agentName;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
