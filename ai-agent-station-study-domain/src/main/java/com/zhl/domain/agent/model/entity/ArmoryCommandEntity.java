package com.zhl.domain.agent.model.entity;

import com.zhl.domain.agent.model.valobj.AiAgentEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 *  装配命令
 * @author zhl
 * @since 2026-04-08 11:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArmoryCommandEntity {

    /**
     * 命令类型
     */
    private String commandType;

    /**
     * 命令索引（clientId、modelId、apiId...）
     */
    private List<String> commandIdList;


    public String getLoadDataStrategyKey() {
        return AiAgentEnumVO.getByCode(commandType).getLoadDataStrategy();
    }

}
