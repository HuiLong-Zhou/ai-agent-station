package com.zhl.types.job.provider;

import com.zhl.types.job.model.TaskScheduleVO;

import java.util.List;

/**
 * <p>
 *  任务数据提供者接口，用户需要实现此接口来提供任务调度数据
 *
 *  由业务模块提供有效任务配置，避免调度框架依赖具体 Agent 业务。
 * @author zhl
 * @since 2026-05-19 21:42
 */
public interface ITaskDataProvider {

    /**
     * 查询所有有效的任务调度配置
     * @return 任务调度配置列表
     */
    List<TaskScheduleVO> queryAllValidTaskSchedule();

    /**
     * 查询所有无效的任务ID
     * @return 无效任务ID列表
     */
    List<Long> queryAllInvalidTaskScheduleIds();

}
