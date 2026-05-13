package com.zhl.domain.agent.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  执行命令实体
 * @author zhl
 * @since 2026-05-12 15:08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCommandEntity {

    private String aiAgentId;

    private String message;

    private String sessionId;

    private Integer maxStep;

}
