package com.zhl.trigger.http;

import com.alibaba.fastjson2.JSON;
import com.zhl.api.IAiAgentService;
import com.zhl.api.dto.AiAgentResponseDTO;
import com.zhl.api.dto.ArmoryAgentRequestDTO;
import com.zhl.api.dto.AutoAgentRequestDTO;
import com.zhl.api.response.Response;
import com.zhl.domain.agent.model.entity.ExecuteCommandEntity;
import com.zhl.domain.agent.model.valobj.AiAgentVO;
import com.zhl.domain.agent.service.IAgentDispatchService;
import com.zhl.domain.agent.service.IArmoryService;
import com.zhl.types.enums.ResponseCode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  AutoAgent 自动智能对话体
 * @author zhl
 * @since 2026-05-13 12:02
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/agent")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AiAgentController implements IAiAgentService {

    @Resource
    private IAgentDispatchService agentDispatchService;

    @Resource
    private IArmoryService armoryService;

    @RequestMapping(value = "auto_agent", method = RequestMethod.POST)
    public ResponseBodyEmitter autoAgent(@RequestBody AutoAgentRequestDTO request, HttpServletResponse response) {
        log.info("AutoAgent流式执行请求开始，请求信息：{}", JSON.toJSONString(request));

        try {
            // 设置SSE响应头
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Connection", "keep-alive");

            // 1. 创建流式输出对象
            ResponseBodyEmitter emitter = new ResponseBodyEmitter(Long.MAX_VALUE);

            // 2. 构建执行命令实体
            ExecuteCommandEntity executeCommandEntity = ExecuteCommandEntity.builder()
                .aiAgentId(request.getAiAgentId())
                .message(request.getMessage())
                .sessionId(request.getSessionId())
                .maxStep(request.getMaxStep())
                .build();

            // 3. 调度处理
            agentDispatchService.dispatch(executeCommandEntity, emitter);

            return emitter;

        } catch (Exception e) {
            log.error("AutoAgent请求处理异常：{}", e.getMessage(), e);
            ResponseBodyEmitter errorEmitter = new ResponseBodyEmitter();
            try {
                errorEmitter.send("请求处理异常：" + e.getMessage());
                errorEmitter.complete();
            } catch (Exception ex) {
                log.error("发送错误信息失败：{}", ex.getMessage(), ex);
            }
            return errorEmitter;
        }
    }

    @RequestMapping(value = "armory_agent", method = RequestMethod.POST)
    @Override
    public Response<Boolean> armoryAgent(@RequestBody ArmoryAgentRequestDTO request) {
        log.info("装配智能体请求开始，请求信息：{}", JSON.toJSONString(request));

        try {
            // 参数校验
            if (request == null || request.getAgentId() == null || request.getAgentId().trim().isEmpty()) {
                log.warn("装配智能体请求参数无效：agentId为空");
                return Response.<Boolean>builder()
                    .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                    .info("agentId不能为空")
                    .data(false)
                    .build();
            }

            // 调用装配服务
            armoryService.acceptArmoryAgent(request.getAgentId());

            log.info("装配智能体成功，agentId：{}", request.getAgentId());
            return Response.<Boolean>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info("装配成功")
                .data(true)
                .build();

        } catch (Exception e) {
            log.error("装配智能体失败，agentId：{}，错误信息：{}",
                request != null ? request.getAgentId() : "null", e.getMessage(), e);
            return Response.<Boolean>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info("装配失败：" + e.getMessage())
                .data(false)
                .build();
        }
    }

    @RequestMapping(value = "query_available_agents", method = RequestMethod.GET)
    @Override
    public Response<List<AiAgentResponseDTO>> queryAvailableAgents() {
        log.info("查询可用智能体列表请求开始");

        try {
            // 调用装配服务查询可用智能体
            List<AiAgentVO> aiAgentVOList = armoryService.queryAvailableAgents();

            // 转换为响应DTO
            List<AiAgentResponseDTO> responseList = new ArrayList<>();
            for (AiAgentVO aiAgentVO : aiAgentVOList) {
                AiAgentResponseDTO responseDTO = AiAgentResponseDTO.builder()
                    .agentId(aiAgentVO.getAgentId())
                    .agentName(aiAgentVO.getAgentName())
                    .description(aiAgentVO.getDescription())
                    .channel(aiAgentVO.getChannel())
                    .strategy(aiAgentVO.getStrategy())
                    .status(aiAgentVO.getStatus())
                    .build();
                responseList.add(responseDTO);
            }

            log.info("查询可用智能体列表成功，共{}个智能体", responseList.size());
            return Response.<List<AiAgentResponseDTO>>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info("查询成功")
                .data(responseList)
                .build();

        } catch (Exception e) {
            log.error("查询可用智能体列表失败，错误信息：{}", e.getMessage(), e);
            return Response.<List<AiAgentResponseDTO>>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info("查询失败：" + e.getMessage())
                .data(new ArrayList<>())
                .build();
        }
    }

}

