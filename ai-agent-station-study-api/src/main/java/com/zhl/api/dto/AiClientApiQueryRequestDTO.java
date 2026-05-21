package com.zhl.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI Client API查询请求 DTO
 *
 * @author zhl
 * @description AI Client API查询请求数据传输对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiClientApiQueryRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *  API ID
     */
    private String apiId;

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
