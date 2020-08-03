package com.fit2cloud.commons.server.process;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.FlowTaskMapper;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtFlowMapper;
import com.fit2cloud.commons.server.constants.FlowLinkValuePermissionMode;
import com.fit2cloud.commons.server.constants.ProcessConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.process.dto.*;
import com.fit2cloud.commons.server.process.lock.ProcessLock;
import com.fit2cloud.commons.server.process.request.ListTaskRequest;
import com.fit2cloud.commons.server.service.MicroService;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.server.service.RoleCommonService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import static com.fit2cloud.commons.server.constants.ResourceTypeConstants.ORGANIZATION;
import static com.fit2cloud.commons.server.constants.ResourceTypeConstants.WORKSPACE;

@Service
@Transactional(rollbackFor = Exception.class)
public class TaskService {

    @Value("${spring.application.name:null}")
    private String moduleId;

    @Resource
    private FlowTaskMapper flowTaskMapper;

    @Resource
    private ExtFlowMapper extFlowMapper;

    @Resource
    private RoleCommonService roleCommonService;

    @Resource
    private UserCommonService userCommonService;

    @Resource
    private ProcessService processService;

    @Resource
    private ProcessLogService processLogService;

    @Resource
    private ProcessModelService processModelService;

    @Resource
    private ProcessEventService processEventService;

    @Resource
    private ProcessMessageService processMessageService;

    @Resource
    private ProcessRoleService processRoleService;

    @Resource
    private ProcessLock processLock;

    @Resource
    private WorkspaceMapper workspaceMapper;

    @Resource
    private MicroService microService;

    @Resource
    private ProcessLinkService processLinkService;

    void startTask(FlowProcess process, ProcessModelDTO model, ProcessDTO processDTO) {
        List<ActivityDTO> activities = model.getActivities();

        ActivityDTO start = activities.get(0);
        FlowTask task = new FlowTask();
        task.setProcessId(process.getProcessId());
        task.setWorkspaceId(process.getWorkspaceId());
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskName(start.getName());
        task.setTaskStep(start.getStep());
        task.setTaskActivity(start.getActivityId());
        task.setTaskAssignee(processDTO.getCreator());
        task.setTaskExecutor(processDTO.getCreator());
        task.setTaskStartTime(System.currentTimeMillis());
        task.setTaskEndTime(System.currentTimeMillis());
        task.setTaskStatus(ProcessConstants.TaskStatus.COMPLETED.name());
        task.setBusinessType(process.getBusinessType());
        task.setBusinessKey(process.getBusinessKey());
        task.setTaskRemarks(start.getName());
        task.setModule(process.getModule());
        flowTaskMapper.insert(task);
        processLogService.saveProcessLog(task);

        // 任务完成事件
        processEventService.triggerTaskEvent(processDTO.getModelId(), task, ProcessConstants.EventOperation.COMPLETE.name());

        // 任务完成消息
        Map<String, Object> map = new HashMap<>();
        getParameters(process.getProcessId(), null, map);
        processMessageService.sendTaskMessage(processDTO.getModelId(), task, ProcessConstants.MessageOperation.COMPLETE.name(), map, receiver -> {
            map.put("TASK_ID", task.getTaskId());
            map.put("STEP", task.getTaskStep());
        });

        nextTask(process, model, task, processDTO.getData(), processDTO.getNextStepAssignees());
    }

    private List<FlowTask> backToLink(String processId, String taskId, List<String> nextStepAssignees) {
        FlowTask task = flowTaskMapper.selectByPrimaryKey(taskId);
        FlowProcess process = processService.getProcessById(processId);
        ProcessModelDTO model = processModelService.getProcessModel(processId);
        ActivityDTO activity = null;
        for (ActivityDTO modelActivity : model.getActivities()) {
            if(modelActivity.getActivityId().equalsIgnoreCase(task.getTaskActivity())){
                activity = modelActivity;
            }
        }
        if (activity == null) {
            throw new RuntimeException("Can not find link!");
        }
        FlowTask lastTask = new FlowTask();
        lastTask.setTaskStep(activity.getStep());
        if(nextStepAssignees == null){
            nextStepAssignees = new ArrayList<>(  );
        }
        nextStepAssignees.add( task.getTaskExecutor() );
        return nextTask(activity, process, model, lastTask, null, nextStepAssignees);
    }

    private List<FlowTask> nextTask(FlowProcess process, ProcessModelDTO model, FlowTask lastTask, List<ProcessDataDTO> data, List<String> nextStepAssignees) {
        List<ActivityDTO> activities = model.getActivities();
        ActivityDTO last = activities.get(activities.size() - 1);

        // 没有后续环节，流程结束
        if (lastTask.getTaskStep() >= last.getStep()) {
            processService.completeProcess(process);
            return null;
        }

        int index = indexOf(activities, lastTask.getTaskStep()) + 1;
        if (index == 0) return null;
        ActivityDTO activity = activities.get(index);
        return nextTask(activity, process, model, lastTask, data, nextStepAssignees);
    }

    private List<FlowTask> nextTask(ActivityDTO activity, FlowProcess process, ProcessModelDTO model, FlowTask lastTask, List<ProcessDataDTO> data, List<String> nextStepAssignees) {
        List<String> assignees = new ArrayList<>();
        // 根据 优先级、作用范围、判断是否跳过此环节；
        // 查找assignees；

        if(StringUtils.isEmpty(activity.getLinkType()) || activity.getLinkType().equalsIgnoreCase(ProcessConstants.ModelLinkType.CUSTOMIZE.toString())){
            assignees = getAssignees(process, activity.getAssignee(), activity.getAssigneeType(), data);
        }else {
            FlowLinkValue flowLinkValue = filterProcessFlowLinkValue(process, activity);
            if(flowLinkValue == null){
                //跳过此环节；
                lastTask.setTaskStep(lastTask.getTaskStep() + 1);
                return nextTask(process, model, lastTask, data, nextStepAssignees);
            }
            assignees = getAssignees(process, flowLinkValue.getAssignee(), flowLinkValue.getAssigneeType(), data);
        }

        if (CollectionUtils.isEmpty(assignees)) {
            if (activity.isJump()) {
                return jump(process, model, activity, data);
            } else {
                throw new RuntimeException("Task assignee is required");
            }
        }

        //提交订单时，指定的审批人
        if (nextStepAssignees != null && nextStepAssignees.size() > 0 && assignees.containsAll(nextStepAssignees)) {
            assignees = nextStepAssignees;
        }

        List<FlowTask> tasks = createTasks(assignees, activity, process);
        Map<String, Object> map = new HashMap<>();
        String modelId = processModelService.getModelIdByDeployId(process.getDeployId());

        // 任务到达事件
        processEventService.triggerTaskEvent(modelId, tasks, ProcessConstants.EventOperation.PENDING.name());

        // 任务到达消息
        getParameters(process.getProcessId(), tasks, map);
        processMessageService.sendTaskMessage(modelId, tasks.get(0), ProcessConstants.MessageOperation.PENDING.name(), map, receiver -> tasks.forEach(task -> {
            if (StringUtils.equals(task.getTaskAssignee(), receiver)) {
                map.put("TASK_ID", task.getTaskId());
                map.put("STEP", task.getTaskStep());
            }
        }));
        return tasks;
    }

    private void nextTask(FlowTask lastTask, List<ProcessDataDTO> data, List<String> nextStepAssignees) {
        FlowProcess process = processService.getProcessById(lastTask.getProcessId());
        ProcessModelDTO model = processModelService.getProcessModel(lastTask.getProcessId());
        nextTask(process, model, lastTask, data, nextStepAssignees);
    }

    private List<FlowTask> jump(FlowProcess process, ProcessModelDTO model, ActivityDTO activity, List<ProcessDataDTO> data) {
        FlowTask task = new FlowTask();
        task.setProcessId(process.getProcessId());
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskName(activity.getName());
        task.setTaskStep(activity.getStep());
        task.setTaskStartTime(System.currentTimeMillis());
        task.setTaskEndTime(System.currentTimeMillis());
        task.setTaskStatus(ProcessConstants.TaskStatus.COMPLETED.name());
        task.setTaskFormUrl(activity.getUrl());
        task.setBusinessType(process.getBusinessType());
        task.setBusinessKey(process.getBusinessKey());
        task.setTaskAssignee(ProcessConstants.SYSTEM);
        task.setTaskExecutor(ProcessConstants.SYSTEM);
        task.setTaskRemarks(Translator.toI18nKey("i18n_order_no_executor"));
        flowTaskMapper.insert(task);
        processLogService.saveProcessLog(task);
        return nextTask(process, model, task, data, null);
    }

    private FlowLinkValue filterProcessFlowLinkValue(FlowProcess process, ActivityDTO activity){
        String flowLinkKey = activity.getLinkKey();
        if(StringUtils.isEmpty(flowLinkKey)){
            return null;
        }
        FlowLink flowLink = processLinkService.findFlowLinkByLinkKey(flowLinkKey);
        if(flowLink == null || !flowLink.getEnable()){
            return null;
        }
        Workspace workspace = workspaceMapper.selectByPrimaryKey(process.getWorkspaceId());
        List<FlowLinkValue> flowLinkValues = processLinkService.listProcessLinkValues(flowLinkKey);
        flowLinkValues.sort(Comparator.comparing(FlowLinkValue::getLinkValuePriority));

        for (FlowLinkValue flowLinkValue : flowLinkValues) {
            List<FlowLinkValueScope> flowLinkValueScopes = processLinkService.getLinkValueWorkspaces(flowLinkValue.getId());
            List<String> workSpaceIdList = flowLinkValueScopes.parallelStream()
                    .filter(flowLinkValueScope -> flowLinkValueScope.getType().equalsIgnoreCase(WORKSPACE.name()))
                    .map(FlowLinkValueScope::getWorkspaceId).collect(Collectors.toList());
            List<String> organizationIdList = flowLinkValueScopes.parallelStream()
                    .filter(flowLinkValueScope -> flowLinkValueScope.getType().equalsIgnoreCase(ORGANIZATION.name()))
                    .map(FlowLinkValueScope::getWorkspaceId).collect(Collectors.toList());
            if(StringUtils.isNotEmpty(flowLinkValue.getPermissionMode())
                    && flowLinkValue.getPermissionMode().equalsIgnoreCase(FlowLinkValuePermissionMode.WHITELIST.name())){
                if(workSpaceIdList.contains(process.getWorkspaceId()) || organizationIdList.contains(workspace.getOrganizationId())){
                    if (getFlowLinkValue(flowLinkValue, flowLink, process) != null) {
                        return flowLinkValue;
                    }
                }
            }
            if(StringUtils.isNotEmpty(flowLinkValue.getPermissionMode())
                    && flowLinkValue.getPermissionMode().equalsIgnoreCase(FlowLinkValuePermissionMode.BLACKLIST.name())){
                if(!workSpaceIdList.contains(process.getWorkspaceId()) && !organizationIdList.contains(workspace.getOrganizationId())){
                    if (getFlowLinkValue(flowLinkValue, flowLink, process) != null) {
                        return flowLinkValue;
                    }
                }
            }
        }
        return null;
    }

    private FlowLinkValue getFlowLinkValue (FlowLinkValue flowLinkValue, FlowLink flowLink, FlowProcess process){
        if(StringUtils.isEmpty(flowLink.getLinkType()) || flowLink.getLinkType().equalsIgnoreCase(ProcessConstants.LinkType.CUSTOM.toString())){
            //自定义环节
            return flowLinkValue;
        } else {
            //系统内置环节
            ProcessLinkValueHandle processLinkValueHandle = CommonBeanFactory.getBean(ProcessLinkValueHandle.class);
            if(processLinkValueHandle.execute(process, flowLinkValue)){
                return flowLinkValue;
            }
        }
        return null;
    }

//    private List<String> getAssignees(FlowProcess process, ActivityDTO activity, List<ProcessDataDTO> data) {
    private List<String> getAssignees(FlowProcess process, String activityAssignee,  String assigneeType, List<ProcessDataDTO> data) {
//        String activityAssignee = activity.getAssignee();
//        String assigneeType = activity.getAssigneeType();
        List<String> list = new ArrayList<>();

        // 用户或角色
        String assignee;

        // 申请人
        if (StringUtils.equals(activityAssignee, ProcessConstants.OWNER)) {
            assignee = process.getProcessCreator();
            CollectionUtils.addIgnoreNull(list, assignee);
        }

        // 其他用户
        if (StringUtils.equals(assigneeType, ProcessConstants.AssigneeType.USER.name())) {
            if (StringUtils.isNotBlank(activityAssignee)) {
                String[] users = activityAssignee.split(",");
                list.addAll(Arrays.asList(users));
            }
        }

        // 流程变量
        if (StringUtils.equals(assigneeType, ProcessConstants.AssigneeType.VARIABLES.name())) {
            assignee = getProcessDataValue(process.getProcessId(), activityAssignee, data);
            if (assignee != null) {
                String[] users = assignee.split(",");
                list.addAll(Arrays.asList(users));
            }
        }

        // 系统角色
        if (StringUtils.equals(assigneeType, ProcessConstants.AssigneeType.SYSTEM_ROLE.name())) {
            if (roleCommonService.isExtendAdmin(activityAssignee)) {
                list.addAll(roleCommonService.listAdmins(activityAssignee));
            }

            if (roleCommonService.isExtendOrgAdmin(activityAssignee)) {
                list.addAll(roleCommonService.listOrgAdmins(activityAssignee, process.getWorkspaceId()));
            }

            if (roleCommonService.isExtendUser(activityAssignee)) {
                list.addAll(roleCommonService.listUsers(activityAssignee, process.getWorkspaceId()));
            }
        }

        // 流程角色
        if (StringUtils.equals(assigneeType, ProcessConstants.AssigneeType.PROCESS_ROLE.name())) {
            List<FlowRoleUser> roleUsers = processRoleService.listRoleUsers(activityAssignee);
            roleUsers.forEach(roleUser -> CollectionUtils.addIgnoreNull(list, roleUser.getUserId()));
        }

        return list;
    }

    private String getProcessDataValue(String processId, String dataName, List<ProcessDataDTO> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            for (ProcessDataDTO processDataDTO : data) {
                if (processDataDTO.getDataName().equals(dataName)) {
                    return processDataDTO.getDataValue();
                }
            }
        }
        FlowProcessData processData = extFlowMapper.getProcessDataValue(processId, dataName);
        if (processData == null) return null;
        return processData.getDataValue();
    }

    public int indexOf(List<ActivityDTO> activities, int step) {
        for (int i = 0; i < activities.size(); i++) {
            if (step == activities.get(i).getStep()) {
                return i;
            }
        }
        return -1;
    }

    private List<FlowTask> createTasks(List<String> assignees, ActivityDTO activity, FlowProcess process) {
        List<FlowTask> tasks = new ArrayList<>();

        for (String assignee : assignees) {
            FlowTask task = new FlowTask();
            task.setProcessId(process.getProcessId());
            task.setWorkspaceId(process.getWorkspaceId());
            task.setTaskActivity(activity.getActivityId());
            task.setTaskId(UUID.randomUUID().toString());
            task.setTaskStep(activity.getStep());
            task.setTaskName(activity.getName());
            task.setTaskStartTime(System.currentTimeMillis());
            task.setTaskStatus(ProcessConstants.TaskStatus.PENDING.name());
            task.setTaskFormUrl(activity.getUrl());
            task.setBusinessType(process.getBusinessType());
            task.setBusinessKey(process.getBusinessKey());
            task.setTaskAssignee(assignee);
            task.setModule(process.getModule());
            tasks.add(task);
        }
        extFlowMapper.insertTaskBatch(tasks);

        return tasks;
    }

    private List<FlowTask> getSameActivityTask(FlowTask task) {
        FlowTaskExample example = new FlowTaskExample();
        FlowTaskExample.Criteria criteria = example.createCriteria();
        criteria.andProcessIdEqualTo(task.getProcessId());
        criteria.andTaskActivityEqualTo(task.getTaskActivity());
        criteria.andTaskIdNotEqualTo(task.getTaskId());
        return flowTaskMapper.selectByExample(example);
    }

    private void completeMultipleTask(FlowTask task) {
        List<FlowTask> list = getSameActivityTask(task);
        for (FlowTask flowTask : list) {
            flowTask.setTaskStatus(ProcessConstants.TaskStatus.COMPLETED.name());
            flowTask.setTaskEndTime(System.currentTimeMillis());
            flowTask.setTaskExecutor(task.getTaskExecutor());
            flowTask.setTaskRemarks(task.getTaskRemarks());
            flowTaskMapper.updateByPrimaryKeySelective(flowTask);
        }
    }

    private void checkStatus(FlowTask task) {
        FlowProcess process = processService.getProcessById(task.getProcessId());
        if (!StringUtils.equals(process.getProcessStatus(), ProcessConstants.ProcessStatus.PENDING.name())) {
            throw new RuntimeException("Process status is not pending，status:" + process.getProcessStatus());
        }
        FlowTask currentTask = flowTaskMapper.selectByPrimaryKey(task.getTaskId());
        if (!StringUtils.equals(currentTask.getTaskStatus(), ProcessConstants.TaskStatus.PENDING.name())) {
            throw new RuntimeException("Task status is not pending，status:" + currentTask.getTaskStatus());
        }
        if (StringUtils.isBlank(task.getTaskExecutor())) {
            try {
                task.setTaskExecutor(Objects.requireNonNull(SessionUtils.getUser()).getId());
            } catch (Exception e) {
                throw new RuntimeException("Task executor is required");
            }
        }
        if (!StringUtils.equals(currentTask.getTaskAssignee(), task.getTaskExecutor())) {
            throw new RuntimeException("The user is not task assignee, user: " + task.getTaskExecutor());
        }
    }

    public FlowTask getTask(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            throw new RuntimeException("Task id is required.");
        }
        return flowTaskMapper.selectByPrimaryKey(taskId);
    }

    public void completeWithAssignees(FlowTask task, List<ProcessDataDTO> data, List<String> nextStepAssignees) {
        String processId = task.getProcessId();

        if (processLock.lock(processId)) {
            try {
                checkStatus(task);
                FlowProcess process = processService.getProcessById(processId);
                Map<String, Object> map = new HashMap<>();
                String modelId = processModelService.getModelIdByDeployId(processService.getProcessById(task.getProcessId()).getDeployId());
                task.setWorkspaceId(process.getWorkspaceId());
                task.setTaskStatus(ProcessConstants.TaskStatus.COMPLETED.name());
                task.setTaskEndTime(System.currentTimeMillis());
                flowTaskMapper.updateByPrimaryKeySelective(task);
                OperationLogService.log(workspaceMapper.selectByPrimaryKey(process.getWorkspaceId()), task.getTaskId(), Translator.toI18nKey(process.getProcessName()) + "-" + Translator.toI18nKey(task.getTaskName()), "TASK", Translator.toI18nKey("i18n_task_complete"), null);
                processLogService.saveProcessLog(task);
                completeMultipleTask(task);

                // 任务完成事件
                processEventService.triggerTaskEvent(modelId, task, ProcessConstants.EventOperation.COMPLETE.name());
                processService.saveProcessData(processId, data);

                // 任务完成消息
                getParameters(processId, null, map);
                processMessageService.sendTaskMessage(modelId, task, ProcessConstants.MessageOperation.COMPLETE.name(), map, receiver -> {
                    map.put("TASK_ID", task.getTaskId());
                    map.put("STEP", task.getTaskStep());
                });

                nextTask(task, data, nextStepAssignees);
            } finally {
                processLock.unlock(processId);
            }
        } else {
            throw new RuntimeException("complete conflict");
        }
    }

    public void complete(FlowTask task, List<ProcessDataDTO> data) {
        completeWithAssignees(task, data, null);
    }

    public void reject(FlowTask task) {
        if (processLock.lock(task.getProcessId())) {
            try {
                checkStatus(task);
                task.setTaskEndTime(System.currentTimeMillis());
                flowTaskMapper.updateByPrimaryKeySelective(task);
                FlowProcess process = processService.getProcessById(task.getProcessId());
                OperationLogService.log(workspaceMapper.selectByPrimaryKey(process.getWorkspaceId()), task.getTaskId(), Translator.toI18nKey(process.getProcessName()) + "-" + Translator.toI18nKey(task.getTaskName()), "TASK", Translator.toI18nKey("i18n_task_reject"), null);
                processService.terminateProcess(task);
            } finally {
                processLock.unlock(task.getProcessId());
            }
        } else {
            throw new RuntimeException("reject conflict");
        }
    }

    public void backToLink(FlowTask task, String activityId, List<String> nextStepAssignees) {
        String processId = task.getProcessId();
        if (processLock.lock(processId)) {
            try {
                checkStatus(task);
                FlowProcess process = processService.getProcessById(processId);
                task.setWorkspaceId(process.getWorkspaceId());
                task.setTaskStatus(ProcessConstants.TaskStatus.COMPLETED.name());
                task.setTaskEndTime(System.currentTimeMillis());
                flowTaskMapper.updateByPrimaryKeySelective(task);
                OperationLogService.log(workspaceMapper.selectByPrimaryKey(process.getWorkspaceId()), task.getTaskId(), Translator.toI18nKey(process.getProcessName()) + "-" + Translator.toI18nKey(task.getTaskName()), "TASK", Translator.toI18nKey("i18n_task_complete"), null);
                processLogService.saveProcessLog(task);
                completeMultipleTask(task);
                backToLink(processId, activityId, nextStepAssignees);
            } finally {
                processLock.unlock(processId);
            }
        } else {
            throw new RuntimeException("BackToTask conflict");
        }
    }



    void terminate(FlowTask task) {
        String processId = task.getProcessId();
        FlowTaskExample example = new FlowTaskExample();
        example.createCriteria().andProcessIdEqualTo(processId);
        List<FlowTask> tasks = flowTaskMapper.selectByExample(example);
        for (FlowTask flowTask : tasks) {
            if (!flowTask.getTaskStatus().equals(ProcessConstants.TaskStatus.COMPLETED.name())) {
                flowTask.setTaskStatus(ProcessConstants.TaskStatus.TERMINATED.name());
                flowTask.setTaskEndTime(System.currentTimeMillis());
                flowTask.setTaskExecutor(task.getTaskExecutor());
                flowTaskMapper.updateByPrimaryKeySelective(flowTask);
            }
        }
        processLogService.saveProcessLog(task);
    }

    void cancel(String processId, String userId) {
        if (processLock.lock(processId)) {
            try {
                FlowTaskExample example = new FlowTaskExample();
                example.createCriteria().andProcessIdEqualTo(processId).andTaskStatusEqualTo(ProcessConstants.TaskStatus.PENDING.name());
                List<FlowTask> tasks = flowTaskMapper.selectByExample(example);
                for (FlowTask flowTask : tasks) {
                    flowTask.setTaskStatus(ProcessConstants.TaskStatus.CANCEL.name());
                    flowTask.setTaskEndTime(System.currentTimeMillis());
                    flowTask.setTaskExecutor(userId);
                    flowTask.setTaskRemarks("撤销");
                    flowTaskMapper.updateByPrimaryKeySelective(flowTask);
                }
                processLogService.saveProcessLog(tasks.get(0));
            } finally {
                processLock.unlock(processId);
            }
        } else {
            throw new RuntimeException("cancel conflict");
        }
    }

    private void getParameters(String processId, List<FlowTask> tasks, Map<String, Object> map) {
        FlowProcess process = processService.getProcessById(processId);
        map.put("CREATOR", process.getProcessCreator());
        map.put("CREATOR_NAME", userCommonService.getUserName(process.getProcessCreator()));
        map.put("PROCESS_NAME", process.getProcessName());
        map.put("PROCESS_ID", processId);
        map.put("BUSINESS_KEY", process.getBusinessKey());
        if (tasks == null || tasks.size() == 0) return;
        addAssignee(tasks, map);
    }

    void addAssignee(List<FlowTask> list, Map<String, Object> map) {
        if (CollectionUtils.isNotEmpty(list)) {
            StringBuilder assignees = new StringBuilder();
            StringBuilder names = new StringBuilder();
            for (FlowTask FlowTask : list) {
                String name = userCommonService.getUserName(FlowTask.getTaskAssignee());
                if (StringUtils.isNotBlank(name)) {
                    assignees.append(",").append(FlowTask.getTaskAssignee());
                    names.append(",").append(name);
                }
            }
            map.put("ASSIGNEE", assignees.toString());
            map.put("ASSIGNEE_NAME", names.toString());
        }
    }

    public List<TaskDTO> listPendingTask(ListTaskRequest request) {
        String search = null;
        if (StringUtils.isNotBlank(request.getSearch())) {
            search = "%" + request.getSearch() + "%";
        }
        return extFlowMapper.listPendingTask(Objects.requireNonNull(SessionUtils.getUser()).getId(), search);
    }

    public List<TaskDTO> listEndTask(ListTaskRequest request) {
        String search = null;
        if (StringUtils.isNotBlank(request.getSearch())) {
            search = "%" + request.getSearch() + "%";
        }
        return extFlowMapper.listEndTask(Objects.requireNonNull(SessionUtils.getUser()).getId(), search);
    }

    public List<FlowProcessLog> listTaskLog(String processId) {
        List<FlowProcessLog> list = processLogService.listProcessLog(processId);
        List<FlowTask> tasks = extFlowMapper.listTaskLog(processId);
        for (FlowTask task : tasks) {
            if (StringUtils.isBlank(task.getTaskExecutor())) {
                task.setTaskExecutor(task.getTaskAssignee());
            }
            FlowProcessLog processLog = new FlowProcessLog();
            BeanUtils.copyBean(processLog, task);
            list.add(0, processLog);
        }
        return list;
    }

    List<FlowTask> listCurrentTask(String processId) {
        FlowTaskExample example = new FlowTaskExample();
        example.createCriteria().andProcessIdEqualTo(processId).andTaskStatusEqualTo(ProcessConstants.TaskStatus.PENDING.name());
        return flowTaskMapper.selectByExample(example);
    }

    public FlowTask getLastTaskByAssignee(String processId, String assignee) {
        FlowTaskExample example = new FlowTaskExample();
        example.setOrderByClause("task_step desc");
        example.createCriteria().andProcessIdEqualTo(processId).andTaskAssigneeEqualTo(assignee).andTaskStatusEqualTo(ProcessConstants.TaskStatus.PENDING.name());
        List<FlowTask> list = flowTaskMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public boolean dispatcher(FlowTask flowTask, String url, Object param) {
        if (StringUtils.equalsIgnoreCase(moduleId, flowTask.getModule())) {
            return false;
        }
        ResultHolder holder = microService.postForResultHolder(flowTask.getModule(), url, param);
        if (!holder.isSuccess()) {
            F2CException.throwException(holder.getMessage());
        }
        return true;
    }

    public List<User>getStepAssignees(String processId, String modelId, Integer taskStep){
        if(taskStep < 0){
            return null;
        }
        FlowDeploy deploy = processModelService.getLastVersionDeploy(modelId);
        if (deploy == null) {
            F2CException.throwException("no deploy found by model id: " + modelId);
        }
        ProcessModelDTO model = processModelService.getProcessModel(deploy);
        List<ActivityDTO> activities = model.getActivities();
        ActivityDTO last = activities.get(activities.size() - 1);
        // 没有后续环节，流程结束
        if (taskStep > last.getStep()) {
            return new ArrayList<>();
        }
        ActivityDTO activity = activities.get(taskStep);
        List<String> assignees = new ArrayList<>();
        FlowProcess process = new FlowProcess();
        if(StringUtils.isNotEmpty(processId)){
            process = processService.getProcessById(processId);
        }else {
            process.setModule(moduleId);
            process.setDeployId(deploy.getDeployId());
            process.setWorkspaceId(SessionUtils.getWorkspaceId());
            process.setProcessId(UUIDUtil.newUUID());
        }
        if(StringUtils.isEmpty(activity.getLinkType()) || activity.getLinkType().equalsIgnoreCase(ProcessConstants.ModelLinkType.CUSTOMIZE.toString())){
            assignees = getAssignees(process, activity.getAssignee(), activity.getAssigneeType(), new ArrayList<ProcessDataDTO>());
        }else {
            FlowLinkValue flowLinkValue = filterProcessFlowLinkValue(process, activity);
            if(flowLinkValue == null){
                //跳过此环节；
                return getStepAssignees(processId, modelId, taskStep + 1);
            }
            assignees = getAssignees(process, flowLinkValue.getAssignee(), flowLinkValue.getAssigneeType(), new ArrayList<ProcessDataDTO>());
        }
        if(CollectionUtils.isNotEmpty(assignees)){
            return userCommonService.getUsersByIdList(assignees);
        }
        return new ArrayList<>();
    }

    public List<FlowTask>getLinkBeforeTask(String taskId){
        FlowTask task = flowTaskMapper.selectByPrimaryKey(taskId);
        FlowTaskExample example = new FlowTaskExample();
        example.createCriteria().andBusinessKeyEqualTo(task.getBusinessKey()).andProcessIdEqualTo(task.getProcessId()).andModuleEqualTo(task.getModule()).andTaskIdNotEqualTo(taskId)
                .andTaskStatusEqualTo( ProcessConstants.TaskStatus.COMPLETED.name()).andTaskStepLessThan( task.getTaskStep() );
        List<FlowTask> flowTasks = flowTaskMapper.selectByExample(example);
        flowTasks.sort(Comparator.comparing(FlowTask::getTaskStartTime));
        Collections.reverse(flowTasks);
        return flowTasks.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FlowTask :: getTaskStep))), ArrayList::new));
    }

}
