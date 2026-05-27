package com.zhl.api.service;

import com.zhl.api.dto.AiClientApiQueryRequestDTO;
import com.zhl.api.dto.AiClientApiRequestDTO;
import com.zhl.api.dto.AiClientApiResponseDTO;
import com.zhl.api.response.Response;

import java.util.List;

/**
 * AI Client API管理服务接口
 *
 * @author zhl
 * @description AI Client API配置管理服务接口
 */
public interface IAiClientApiService {

     /**
      * 查询 AiClient Api列表
      * @param request AI客户端 API 配置请求对象
      * @return  AI Client API 信息
      */
     Response<List<AiClientApiResponseDTO>> queryAiClientApiList(AiClientApiQueryRequestDTO request);

     /**
      * 创建AI客户端 API
      * @param request AI客户端 API 配置请求对象
      * @return 操作结果
      */
     Response<Boolean> createAiClientApi(AiClientApiRequestDTO request);

     /**
      * 根据ID删除AI客户端 API配置
      * @param id 主键ID
      * @return 操作结果
      */
     Response<Boolean> deleteAiClientApiById(Long id);

     /**
      * 根据API ID删除AI客户端配置
      * @param apiId API ID
      * @return 操作结果
      */
     Response<Boolean> deleteAiClientApiByApiId(String apiId);

}
