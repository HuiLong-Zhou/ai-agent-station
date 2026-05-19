package com.zhl.domain.agent.service;

import com.zhl.domain.agent.model.valobj.AiAgentVO;

import java.util.List;

/**
 * <p>
 *  装配接口
 * @author zhl
 * @since 2026-05-20 1:03
 */
public interface IArmoryService {

    List<AiAgentVO> acceptArmoryAllAvailableAgents();

    void acceptArmoryAgent(String agentId);

    List<AiAgentVO> queryAvailableAgents();

}
