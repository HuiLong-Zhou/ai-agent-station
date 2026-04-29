package com.zhl.domain.agent.model.entity;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *  装配命令
 * @author zhl
 * @since 2026-04-08 11:39
 */
@Data
public class ArmoryCommandEntity {

    /**
     * 命令类型
     */
    private String commandType;

    /**
     * 命令索引（clientId、modelId、apiId...）
     */
    private List<String> commandIdList;

}
