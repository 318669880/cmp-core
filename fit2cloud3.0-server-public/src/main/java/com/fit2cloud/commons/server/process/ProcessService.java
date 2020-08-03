package com.fit2cloud.commons.server.process;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.FlowProcessDataMapper;
import com.fit2cloud.commons.server.base.mapper.FlowProcessMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtFlowMapper;
import com.fit2cloud.commons.server.constants.ProcessConstants;
import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.process.dto.ActivityDTO;
import com.fit2cloud.commons.server.process.dto.ProcessDTO;
import com.fit2cloud.commons.server.process.dto.ProcessDataDTO;
import com.fit2cloud.commons.server.process.dto.ProcessModelDTO;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessService {

    @Value("${spring.application.name:null}")
    private String moduleId;

    @Resource
    private FlowProcessMapper flowProcessMapper;

    @Resource
    private UserCommonService userCommonService;

    @Resource
    private ProcessModelService processModelService;

    @Resource
    private ProcessEventService processEventService;

    @Resource
    private ProcessMessageService processMessageService;

    @Resource
    private TaskService taskService;

    @Resource
    private ExtFlowMapper extFlowMapper;

    @Resource
    private FlowProcessDataMapper flowProcessDataMapper;

    @Resource
    private WorkspaceMapper workspaceMapper;

    FlowProcess getProcessById(String processId) {
        return flowProcessMapper.selectByPrimaryKey(processId);
    }

    public FlowProcess createProcess(ProcessDTO processDTO) {
        FlowDeploy deploy = processModelService.getLastVersionDeploy(processDTO.getModelId());
        if (deploy == null) {
            F2CException.throwException("no deploy found by model id: " + processDTO.getModelId());
        }
        FlowProcess process = new FlowProcess();
        process.setModule(moduleId);
        process.setDeployId(deploy.getDeployId());
        process.setBusinessKey(processDTO.getBusinessKey());
        process.setProcessCreator(processDTO.getCreator());
        process.setProcessStatus(ProcessConstants.ProcessStatus.PENDING.name());
        process.setProcessStartTime(System.currentTimeMillis());
        process.setBusinessType(processDTO.getBusinessType());
        process.setProcessId(Optional.ofNullable(processDTO.getProcessId()).orElse(UUID.randomUUID().toString()));
        process.setWorkspaceId(Optional.ofNullable(processDTO.getWorkspaceId()).orElse(Objects.requireNonNull(SessionUtils.getUser()).getSourceId()));
        process.setProcessName(Optional.ofNullable(processDTO.getProcessName()).orElseGet(
                () -> processModelService.getModelById(processDTO.getModelId()).getModelName())
        );

        flowProcessMapper.insert(process);

        List<ProcessDataDTO> data = processDTO.getData();
        saveProcessData(process.getProcessId(), data);

        ProcessModelDTO model = processModelService.getProcessModel(deploy);
        taskService.startTask(process, model, processDTO);
        return process;
    }

    public String createProcessWithAssignees(String modelId, String processId, String creator, String businessKey, String businessType, List<String> assignees) {
        ProcessDTO processDTO = new ProcessDTO();
        processDTO.setProcessId(processId);
        processDTO.setModelId(modelId);
        processDTO.setCreator(creator);
        processDTO.setBusinessKey(businessKey);
        processDTO.setBusinessType(businessType);
        processDTO.setNextStepAssignees(assignees);
        return createProcess(processDTO).getProcessId();
    }

    public String createProcess(String modelId, String processId, String creator, String businessKey, String businessType) {
        ProcessDTO processDTO = new ProcessDTO();
        processDTO.setProcessId(processId);
        processDTO.setModelId(modelId);
        processDTO.setCreator(creator);
        processDTO.setBusinessKey(businessKey);
        processDTO.setBusinessType(businessType);
        return createProcess(processDTO).getProcessId();
    }

    public void cancelProcess(String processId, String userId) {
        FlowProcess process = flowProcessMapper.selectByPrimaryKey(processId);
        if (!StringUtils.equals(process.getProcessStatus(), ProcessConstants.ProcessStatus.PENDING.name())) {
            throw new RuntimeException(Translator.get("i18n_ex_process_done"));
        }

        if (!StringUtils.equals(userId, SessionUtils.getUser().getId())) {
            throw new RuntimeException(Translator.get("i18n_ex_initiator_is_wrong"));
        }

        String modelId = processModelService.getModelIdByDeployId(process.getDeployId());

        process.setProcessStatus(ProcessConstants.ProcessStatus.CANCEL.name());
        flowProcessMapper.updateByPrimaryKey(process);
        taskService.cancel(processId, userId);

        // 撤销事件
        processEventService.triggerProcessEvent(modelId, process, ProcessConstants.EventOperation.CANCEL.name());
        Map<String, Object> map = new HashMap<>();
        getParameters(process, map);

        // 撤销消息
        processMessageService.sendProcessMessage(modelId, ProcessConstants.MessageOperation.CANCEL.name(), map);
        OperationLogService.log(workspaceMapper.selectByPrimaryKey(process.getWorkspaceId()), process.getProcessId(), process.getProcessName(), "PROCESS", Translator.toI18nKey("i18n_process_cancel"), null);

    }

    public void terminateProcess(FlowTask task) {
        Map<String, Object> map = new HashMap<>();
        FlowProcess process = flowProcessMapper.selectByPrimaryKey(task.getProcessId());
        String modelId = processModelService.getModelIdByDeployId(process.getDeployId());

        process.setProcessStatus(ProcessConstants.ProcessStatus.TERMINATED.name());
        flowProcessMapper.updateByPrimaryKey(process);
        taskService.terminate(task);

        // 中止事件
        processEventService.triggerProcessEvent(modelId, process, ProcessConstants.EventOperation.TERMINATE.name());

        getParameters(process, map);
        map.put("REJECT_REASON", task.getTaskRemarks());

        // 中止消息
        processMessageService.sendProcessMessage(modelId, ProcessConstants.MessageOperation.TERMINATE.name(), map);
    }

    public void completeProcess(FlowProcess process) {
        Map<String, Object> map = new HashMap<>();
        String modelId = processModelService.getModelIdByDeployId(process.getDeployId());

        process.setProcessStatus(ProcessConstants.ProcessStatus.COMPLETED.name());
        process.setProcessEndTime(System.currentTimeMillis());
        flowProcessMapper.updateByPrimaryKey(process);

        // 完成事件
        processEventService.triggerProcessEvent(modelId, process, ProcessConstants.EventOperation.COMPLETE.name());

        getParameters(process, map);
        // 完成消息
        processMessageService.sendProcessMessage(modelId, ProcessConstants.MessageOperation.COMPLETE.name(), map);
    }

    public void getParameters(FlowProcess process, Map<String, Object> map) {
        map.put("CREATOR", process.getProcessCreator());
        map.put("CREATOR_NAME", userCommonService.getUserName(process.getProcessCreator()));
        map.put("PROCESS_ID", process.getProcessId());
        map.put("PROCESS_NAME", process.getProcessName());
        map.put("BUSINESS_KEY", process.getBusinessKey());
        List<String> executors = extFlowMapper.listTaskExecutor(process.getProcessId());
        addAssignee(executors, map);
    }

    void addAssignee(List<String> executors, Map<String, Object> map) {
        if (CollectionUtils.isNotEmpty(executors)) {
            StringBuilder assignees = new StringBuilder();
            StringBuilder names = new StringBuilder();
            for (String executor : executors) {
                String name = userCommonService.getUserName(executor);
                if (StringUtils.isNotBlank(name)) {
                    assignees.append(",").append(executor);
                    names.append(",").append(name);
                }
            }
            map.put("ASSIGNEE", assignees.toString());
            map.put("ASSIGNEE_NAME", names.toString());
        }
    }

    void saveProcessData(String processId, List<ProcessDataDTO> data) {
        if (data != null && data.size() > 0) {
            for (ProcessDataDTO processDataDTO : data) {
                FlowProcessData processData = new FlowProcessData();
                processData.setProcessId(processId);
                processData.setDataName(processDataDTO.getDataName());
                processData.setDataValue(processDataDTO.getDataValue());
                flowProcessDataMapper.insert(processData);
            }
        }
    }


}
