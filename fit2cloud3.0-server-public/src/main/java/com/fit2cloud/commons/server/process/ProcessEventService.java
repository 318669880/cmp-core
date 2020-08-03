package com.fit2cloud.commons.server.process;

import com.fit2cloud.commons.server.base.domain.FlowEvent;
import com.fit2cloud.commons.server.base.domain.FlowEventExample;
import com.fit2cloud.commons.server.base.domain.FlowProcess;
import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.base.mapper.FlowEventMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtFlowMapper;
import com.fit2cloud.commons.server.constants.ProcessConstants;
import com.fit2cloud.commons.server.process.dto.EventExecutor;
import com.fit2cloud.commons.server.utils.AssignableUtils;
import com.fit2cloud.commons.utils.LogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessEventService {

    @Value("${spring.application.name:null}")
    private String moduleId;

    @Resource
    private FlowEventMapper flowEventMapper;

    @Resource
    private ExtFlowMapper extFlowMapper;

    public List<FlowEvent> listProcessEvent(String modelId, int step, String activityId) {
        if (step == -1) return listProcessEvent(modelId, step);
        return listProcessEvent(modelId, activityId);
    }

    public List<FlowEvent> listProcessEvent(String modelId, int step) {
        FlowEventExample example = new FlowEventExample();
        example.createCriteria().andModelIdEqualTo(modelId).andStepEqualTo(step).andModuleEqualTo(moduleId);
        return flowEventMapper.selectByExampleWithBLOBs(example);
    }

    public List<FlowEvent> listProcessEvent(String modelId, String activityId) {
        FlowEventExample example = new FlowEventExample();
        example.createCriteria().andModelIdEqualTo(modelId).andActivityIdEqualTo(activityId).andModuleEqualTo(moduleId);
        return flowEventMapper.selectByExampleWithBLOBs(example);
    }

    public int addEvent(FlowEvent event) {
        event.setModule(moduleId);
        return flowEventMapper.insert(event);
    }

    public int updateEvent(FlowEvent event) {
        return flowEventMapper.updateByPrimaryKeySelective(event);
    }

    public void copyEvent(String oldId, String newId) {
        FlowEventExample example = new FlowEventExample();
        example.createCriteria().andModelIdEqualTo(oldId);
        List<FlowEvent> list = flowEventMapper.selectByExampleWithBLOBs(example);
        list.forEach(flowEvent -> {
            flowEvent.setId(null);
            flowEvent.setModelId(newId);
            flowEventMapper.insert(flowEvent);
        });
    }

    public int deleteEvent(int id) {
        return flowEventMapper.deleteByPrimaryKey(id);
    }

    public List<EventExecutor> listProcessEventClass() {
        List<EventExecutor> result = new ArrayList<>();
        try {
            Set<Class<?>> classes = AssignableUtils.getClassSet(new String[]{"com.fit2cloud"}, ProcessEvent.class);
            classes.forEach(c -> result.add(new EventExecutor(c.getName(), c.getSimpleName())));
        } catch (Exception e) {
            LogUtil.error("Error getting process event: " + e.getMessage(), e);
        }
        return result;
    }

    void deleteEvent(String modelId) {
        FlowEventExample flowEventExample = new FlowEventExample();
        flowEventExample.createCriteria().andModelIdEqualTo(modelId);
        flowEventMapper.deleteByExample(flowEventExample);
    }

    void triggerTaskEvent(String modelId, List<FlowTask> tasks, String operation) {
        Map<String, Object> map = new HashMap<>();
        map.put("modelId", modelId);
        map.put("activityId", tasks.get(0).getTaskActivity());
        map.put("operation", operation);
        map.put("type", ProcessConstants.EventType.TASK.name());
        map.put("module", moduleId);
        List<FlowEvent> events = extFlowMapper.listProcessEvent(map);
        for (FlowEvent event : events) {
            String className = event.getExecutor();
            Class<?> eventClass = getEventObject(event.getExecutor());
            try {
                ProcessEvent processEvent = (ProcessEvent) eventClass.newInstance();
                ProcessEventContext context = new ProcessEventContext();
                context.setTasks(tasks);
                context.setArguments(event.getArguments());
                processEvent.execute(context);
            } catch (InstantiationException | IllegalAccessException e) {
                LogUtil.error("Event executor type error:" + className);
                throw new RuntimeException("Event executor type error:" + className);
            }
        }
    }

    void triggerTaskEvent(String modelId, FlowTask task, String operation) {
        Map<String, Object> map = new HashMap<>();
        map.put("modelId", modelId);
        map.put("activityId", task.getTaskActivity());
        map.put("operation", operation);
        map.put("type", ProcessConstants.EventType.TASK.name());
        map.put("module", moduleId);
        List<FlowEvent> events = extFlowMapper.listProcessEvent(map);
        for (FlowEvent event : events) {
            String className = event.getExecutor();
            Class<?> eventClass = getEventObject(event.getExecutor());
            try {
                ProcessEvent processEvent = (ProcessEvent) eventClass.newInstance();
                ProcessEventContext context = new ProcessEventContext();
                context.setTask(task);
                context.setArguments(event.getArguments());
                processEvent.execute(context);
            } catch (InstantiationException | IllegalAccessException e) {
                LogUtil.error("Event executor type error:" + className);
                throw new RuntimeException("Event executor type error:" + className);
            }
        }
    }

    void triggerProcessEvent(String modelId, FlowProcess process, String operation) {
        Map<String, Object> map = new HashMap<>();
        map.put("modelId", modelId);
        map.put("operation", operation);
        map.put("type", ProcessConstants.EventType.PROCESS.name());
        map.put("module", moduleId);
        List<FlowEvent> events = extFlowMapper.listProcessEvent(map);
        for (FlowEvent event : events) {
            String className = event.getExecutor();
            Class<?> eventClass = getEventObject(event.getExecutor());
            try {
                ProcessEvent processEvent = (ProcessEvent) eventClass.newInstance();
                ProcessEventContext context = new ProcessEventContext();
                context.setProcess(process);
                context.setArguments(event.getArguments());
                processEvent.execute(context);
            } catch (InstantiationException | IllegalAccessException e) {
                LogUtil.error("Event executor type error:" + className);
                throw new RuntimeException("Event executor type error:" + className);
            }
        }
    }

    private Class<?> getEventObject(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LogUtil.error("Event executor does not exist:" + className);
            throw new RuntimeException("Event executor does not exist:" + className);
        }
    }
}
