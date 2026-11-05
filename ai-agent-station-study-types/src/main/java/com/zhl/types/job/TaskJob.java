package com.zhl.types.job;

import com.zhl.types.job.config.TaskJobAutoProperties;
import com.zhl.types.job.service.ITaskJobService;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * <p>
 * 任务调度作业
 * 定时刷新任务和清理无效任务。
 * @author zhl
 * @since 2026-05-19 21:39
 */
public class TaskJob {

    private final TaskJobAutoProperties properties;
    private final ITaskJobService taskJobService;

    public TaskJob(TaskJobAutoProperties properties, ITaskJobService taskJobService) {
        this.properties = properties;
        this.taskJobService = taskJobService;
    }

    /**
     * 定时刷新任务调度配置
     */
    @Scheduled(fixedRateString = "${xfg.wrench.task.job.refresh-interval:60000}")
    public void refreshTasks() {
        if (!properties.isEnabled()) {
            return;
        }
        taskJobService.refreshTasks();
    }

    /**
     * 定时清理无效任务
     */
    @Scheduled(cron = "${xfg.wrench.task.job.clean-invalid-tasks-cron:0 0/10 * * * ?}")
    public void cleanInvalidTasks() {
        if (!properties.isEnabled()) {
            return;
        }
        taskJobService.cleanInvalidTasks();
    }

}
