package com.zhl.trigger.http.admin;

import com.zhl.api.service.IAiClientApiService;
import com.zhl.api.dto.AiClientApiQueryRequestDTO;
import com.zhl.api.dto.AiClientApiRequestDTO;
import com.zhl.api.dto.AiClientApiResponseDTO;
import com.zhl.api.response.Response;
import com.zhl.infrastructure.dao.IAiClientApiDao;
import com.zhl.infrastructure.dao.po.AiClientApi;
import com.zhl.types.enums.ResponseCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  AI Client API 模型 API 管理
 * @author zhl
 * @since 2026-05-21 20:17
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/ai-client-api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AiClientApiAdminController implements IAiClientApiService {

    @Resource
    private IAiClientApiDao aiClientApiDao;

    @Override
    @PostMapping("/query-list")
    public Response<List<AiClientApiResponseDTO>> queryAiClientApiList(@RequestBody AiClientApiQueryRequestDTO request) {
        try {

            log.info("根据条件查询AI Client API配置列表请求：{}", request);

            List<AiClientApi> aiClientApis;

            // 根据不同条件查询
            if (StringUtils.hasText(request.getApiId())) {
                AiClientApi aiClientApi = aiClientApiDao.queryByApiId(request.getApiId());
                aiClientApis = aiClientApi != null ? List.of(aiClientApi) : List.of();
            }else {
                aiClientApis = aiClientApiDao.queryAll();
            }

            // 状态过滤
            if (request.getStatus() != null) {
                aiClientApis = aiClientApis.stream()
                    .filter(clientApi -> request.getStatus().equals(clientApi.getStatus()))
                    .collect(Collectors.toList());
            }

            // 分页处理（简单实现）
            if (request.getPageNum() != null && request.getPageSize() != null) {
                int start = (request.getPageNum() - 1) * request.getPageSize();
                int end = Math.min(start + request.getPageSize(), aiClientApis.size());
                if (start < aiClientApis.size()) {
                    aiClientApis = aiClientApis.subList(start, end);
                } else {
                    aiClientApis = List.of();
                }
            }

            List<AiClientApiResponseDTO> responseDTOs = aiClientApis.stream()
                .map(this::convertToAiClientApiResponseDTO)
                .collect(Collectors.toList());

            return Response.<List<AiClientApiResponseDTO>>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(responseDTOs)
                .build();

        }catch (Exception e) {
            log.error("根据条件查询AI客户端配置列表失败", e);
            return Response.<List<AiClientApiResponseDTO>>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .data(null)
                .build();
        }
        
    }

    @Override
    @PostMapping("/create")
    public Response<Boolean> createAiClientApi(@RequestBody AiClientApiRequestDTO request) {
        try {
            log.info("创建AI客户端 API 配置请求：{}", request);

            // DTO转PO
            AiClientApi aiClientApi = convertToAiClientApi(request);
            aiClientApi.setCreateTime(LocalDateTime.now());
            aiClientApi.setUpdateTime(LocalDateTime.now());

            int result = aiClientApiDao.insert(aiClientApi);

            return Response.<Boolean>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
        } catch (Exception e) {
            log.error("创建AI客户端 API失败", e);
            return Response.<Boolean>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .data(false)
                .build();
        }
    }


    @Override
    @DeleteMapping("/delete-by-id/{id}")
    public Response<Boolean> deleteAiClientApiById(@PathVariable Long id) {
        try {
            log.info("根据ID删除AI客户端配置请求：{}", id);

            int result = aiClientApiDao.deleteById(id);

            return Response.<Boolean>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
        } catch (Exception e) {
            log.error("根据ID删除AI客户端配置失败", e);
            return Response.<Boolean>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .data(false)
                .build();
        }
    }

    @Override
    @DeleteMapping("/delete-by-api-id/{apiId}")
    public Response<Boolean> deleteAiClientApiByApiId(@PathVariable String apiId) {
        try {
            log.info("根据 APi ID删除AI客户端配置请求：{}", apiId);

            int result = aiClientApiDao.deleteByApiId(apiId);

            return Response.<Boolean>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(result > 0)
                .build();
        } catch (Exception e) {
            log.error("根据API ID删除AI客户端配置失败", e);
            return Response.<Boolean>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .data(false)
                .build();
        }
    }

    /**
     * DTO转PO对象
     */
    private AiClientApi convertToAiClientApi(AiClientApiRequestDTO aiClientApiRequestDTO) {
        AiClientApi aiClientApi = new AiClientApi();
        BeanUtils.copyProperties(aiClientApiRequestDTO, aiClientApi);
        return  aiClientApi;
    }

    /**
     * PO转DTO对象
     */
    private AiClientApiResponseDTO convertToAiClientApiResponseDTO(AiClientApi aiClientApi) {
        AiClientApiResponseDTO responseDTO = new AiClientApiResponseDTO();
        BeanUtils.copyProperties(aiClientApi, responseDTO);
        return responseDTO;
    }
}
