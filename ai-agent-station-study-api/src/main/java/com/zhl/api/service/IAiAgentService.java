package com.zhl.api.service;

import com.zhl.api.dto.AiAgentResponseDTO;
import com.zhl.api.dto.ArmoryAgentRequestDTO;
import com.zhl.api.dto.AutoAgentRequestDTO;
import com.zhl.api.response.Response;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

/**
 * <p>
 *  AI Agent 服务接口
 * @author zhl
 * @since 2026-05-13 12:03
 */
public interface IAiAgentService {

    ResponseBodyEmitter autoAgent(AutoAgentRequestDTO request, HttpServletResponse response);

    /**
     * 装配智能体
     */
    Response<Boolean> armoryAgent(ArmoryAgentRequestDTO request);

    /**
     * 查询可用的智能体列表
     */
    Response<List<AiAgentResponseDTO>> queryAvailableAgents();

}
