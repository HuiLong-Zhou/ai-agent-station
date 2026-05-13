package com.zhl.domain.agent.model.valobj;

import com.zhl.domain.agent.model.valobj.enums.AiAgentEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  AI客户端配置，值对象
 * @author zhl
 * @since 2026-04-08 11:38
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiClientVO {

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 描述
     */
    private String description;

    /**
     * 全局唯一模型ID
     */
    private String modelId;

    /**
     * Prompt ID List
     */
    private List<String> promptIdList;

    /**
     * MCP ID List
     */
    private List<String> mcpIdList;

    /**
     * 顾问ID List
     */
    private List<String> advisorIdList;

    public String getModelBeanName() {
        return AiAgentEnumVO.AI_CLIENT_MODEL.getBeanName(modelId);
    }

    public List<String> getMCPBeanName() {
        List<String> mcpBeanNames = new ArrayList<>();
        for (String mcpId : mcpIdList) {
            mcpBeanNames.add(AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getBeanName(mcpId));
        }
        return mcpBeanNames;
    }

    public List<String> getAdvisorBeanName() {
        List<String> advisorBeanName = new ArrayList<>();
        for (String advisorId : advisorIdList) {
            advisorBeanName.add(AiAgentEnumVO.AI_CLIENT_ADVISOR.getBeanName(advisorId));
        }
        return advisorBeanName;
    }
}

