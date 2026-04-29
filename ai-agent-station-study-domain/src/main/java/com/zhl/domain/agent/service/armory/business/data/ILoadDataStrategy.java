package com.zhl.domain.agent.service.armory.business.data;

import com.zhl.domain.agent.model.entity.ArmoryCommandEntity;
import com.zhl.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;

/**
 * <p>
 *  数据加载策略
 * @author zhl
 * @since 2026-04-08 11:59
 */
public interface ILoadDataStrategy {

    void loadData(ArmoryCommandEntity armoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext dynamicContext);

}

