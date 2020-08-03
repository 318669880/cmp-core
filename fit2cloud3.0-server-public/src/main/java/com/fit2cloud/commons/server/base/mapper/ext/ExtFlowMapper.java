package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.process.dto.TaskDTO;
import com.fit2cloud.commons.server.process.dto.UserDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtFlowMapper {

    FlowProcess getProcessByBusinessKey(@Param("businessKey") String businessKey);

    FlowDeploy getLastVersionDeploy(@Param("modelId") String modelId);

    FlowDeploy getDeployByProcessId(@Param("processId") String processId);

    List<UserDTO> listUser(@Param("search") String search);

    List<UserDTO> listRoleUser(@Param("roleKey") String roleKey);

    FlowProcessData getProcessDataValue(@Param("processId") String processId, @Param("dataName") String dataName);

    List<FlowEvent> listProcessEvent(Map<String, Object> map);

    List<FlowNotificationConfig> listNotificationConfig(Map<String, Object> map);

    FlowTask getLastTask(@Param("processId") String processId);

    List<TaskDTO> listPendingTask(@Param("assignee") String assignee, @Param("search") String search);

    List<TaskDTO> listEndTask(@Param("assignee") String assignee, @Param("search") String search);

    List<FlowTask> listTaskLog(@Param("processId") String processId);

    int insertTaskBatch(List<FlowTask> tasks);

    // 获取环节处理人，不包括开始环节
    List<String> listTaskExecutor(@Param("processId") String processId);

}