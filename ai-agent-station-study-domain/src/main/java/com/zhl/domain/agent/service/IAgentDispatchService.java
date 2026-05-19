package com.zhl.domain.agent.service;

import com.zhl.domain.agent.model.entity.ExecuteCommandEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * <p>
 *  Agent 策略调度器接口
 * @author zhl
 * @since 2026-05-19 16:39
 */
public interface IAgentDispatchService {

    void dispatch(ExecuteCommandEntity requestParameter, ResponseBodyEmitter emitter) throws Exception;

}
