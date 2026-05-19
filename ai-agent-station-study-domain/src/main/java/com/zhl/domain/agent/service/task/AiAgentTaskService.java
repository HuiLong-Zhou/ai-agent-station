package com.zhl.domain.agent.service.task;

import com.zhl.domain.agent.adapter.repository.IAgentRepository;
import com.zhl.domain.agent.model.valobj.AiAgentTaskScheduleVO;
import com.zhl.domain.agent.service.ITaskService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  智能体执行任务
 * @author zhl
 * @since 2026-05-19 22:24
 */
@Service
public class AiAgentTaskService implements ITaskService {

    @Resource
    private IAgentRepository repository;

    @Override
    public List<AiAgentTaskScheduleVO> queryAllValidTaskSchedule() {
        return repository.queryAllValidTaskSchedule();
    }

    @Override
    public List<Long> queryAllInvalidTaskScheduleIds() {
        return repository.queryAllInvalidTaskScheduleIds();
    }

}
