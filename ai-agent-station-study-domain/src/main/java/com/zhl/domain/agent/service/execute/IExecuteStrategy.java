package com.zhl.domain.agent.service.execute;

import com.zhl.domain.agent.model.entity.ExecuteCommandEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * <p>
 *  执行策略接口
 * @author zhl
 * @since 2026-05-12 15:04
 */
public interface IExecuteStrategy {

    void execute(ExecuteCommandEntity requestParameter, ResponseBodyEmitter emitter) throws Exception;

}

