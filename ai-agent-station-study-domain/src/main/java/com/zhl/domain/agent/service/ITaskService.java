package com.zhl.domain.agent.service;

import com.zhl.domain.agent.model.valobj.AiAgentTaskScheduleVO;

import java.util.List;

/**
 * <p>
 *  智能体执行任务
 * @author zhl
 * @since 2026-05-19 21:53
 */
public interface ITaskService {

    List<AiAgentTaskScheduleVO> queryAllValidTaskSchedule();

    List<Long> queryAllInvalidTaskScheduleIds();

}
