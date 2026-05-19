package com.zhl.api;

import com.zhl.api.dto.DataStatisticsResponseDTO;
import com.zhl.api.response.Response;

/**
 * 数据统计
 * @author zhl
 * 2025/10/4 10:33
 */
public interface IAiAgentDataStatisticsAdminService {

    /**
     * 获取系统数据统计
     * @return 统计数据响应
     */
    Response<DataStatisticsResponseDTO> getDataStatistics();
}
