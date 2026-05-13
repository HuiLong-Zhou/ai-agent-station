package com.zhl.domain.agent.adapter.repository;

import com.zhl.domain.agent.model.valobj.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *   AiAgent 仓储接口
 * @author zhl
 * @since 2026-04-08 11:35
 */
public interface IAgentRepository {

    List<AiClientApiVO> queryAiClientApiVOListByClientIds(List<String> clientIdList);

    List<AiClientModelVO> AiClientModelVOByClientIds(List<String> clientIdList);

    List<AiClientToolMcpVO> AiClientToolMcpVOByClientIds(List<String> clientIdList);

    List<AiClientSystemPromptVO> AiClientSystemPromptVOByClientIds(List<String> clientIdList);

    Map<String, AiClientSystemPromptVO> aiClientSystemPromptMapByClientIds(List<String> clientIdList);

    List<AiClientAdvisorVO> AiClientAdvisorVOByClientIds(List<String> clientIdList);

    List<AiClientVO> AiClientVOByClientIds(List<String> clientIdList);

    List<AiClientApiVO> queryAiClientApiVOListByModelIds(List<String> modelIdList);

    List<AiClientModelVO> AiClientModelVOByModelIds(List<String> modelIdList);

    Map<String, AiAgentClientFlowConfigVO> queryAiAgentClientFlowConfig(String aiAgentId);

}
