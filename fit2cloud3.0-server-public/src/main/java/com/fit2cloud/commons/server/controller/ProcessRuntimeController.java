package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.FlowProcessLog;
import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.process.TaskService;
import com.fit2cloud.commons.server.process.dto.TaskDTO;
import com.fit2cloud.commons.server.process.request.CompleteTaskRequest;
import com.fit2cloud.commons.server.process.request.ListTaskRequest;
import com.fit2cloud.commons.server.process.request.RejectTaskRequest;
import com.fit2cloud.commons.server.swagger.annotation.ApiHasModules;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/flow/runtime")
@Api(tags = Translator.PREFIX + "i18n_swagger_task_pending_tag" + Translator.SUFFIX)
public class ProcessRuntimeController {

    @Resource
    private TaskService taskService;

    @ApiOperation(Translator.PREFIX + "i18n_swagger_task_pending_query" + Translator.SUFFIX)
    @I18n(I18nConstants.CLUSTER)
    @ApiHasModules("dashboard")
    @PostMapping(value = "/task/pending/{goPage}/{pageSize}")
    public Pager<List<TaskDTO>> listPendingTask(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ListTaskRequest request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.listPendingTask(request));
    }

    @ApiOperation(Translator.PREFIX + "i18n_swagger_task_done_query" + Translator.SUFFIX)
    @ApiHasModules("dashboard")
    @PostMapping(value = "/task/end/{goPage}/{pageSize}")
    @I18n(I18nConstants.CLUSTER)
    public Pager<List<TaskDTO>> listEndTask(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ListTaskRequest request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.listEndTask(request));
    }

    @ApiOperation(Translator.PREFIX + "i18n_swagger_task_query" + Translator.SUFFIX)
    @ApiHasModules("dashboard")
    @GetMapping(value = "/task/{taskId}")
    @I18n(I18nConstants.CLUSTER)
    public FlowTask getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId);
    }

    @ApiOperation(Translator.PREFIX + "i18n_swagger_task_complete" + Translator.SUFFIX)
    @ApiHasModules("dashboard")
    @PostMapping(value = "/task/complete")
    public void complete(@RequestBody CompleteTaskRequest request) {
        FlowTask flowTask = taskService.getTask(request.getTaskId());
        if (flowTask == null) {
            F2CException.throwException("No task found by id: " + request.getTaskId());
        }
        if (!taskService.dispatcher(flowTask, "/flow/runtime/task/complete", request)) {
            flowTask.setTaskRemarks(request.getRemarks());
            taskService.completeWithAssignees(flowTask, request.getData(), request.getNextStepAssignees());
        }
    }

    @ApiOperation(Translator.PREFIX + "i18n_swagger_task_reject" + Translator.SUFFIX)
    @ApiHasModules("dashboard")
    @PostMapping(value = "/task/reject")
    public void reject(@RequestBody RejectTaskRequest request) {
        FlowTask flowTask = taskService.getTask(request.getTaskId());
        if (flowTask == null) {
            F2CException.throwException("No task found by id: " + request.getTaskId());
        }
        if (!taskService.dispatcher(flowTask, "/flow/runtime/task/reject", request)) {
            flowTask.setTaskRemarks(request.getRemarks());
            taskService.reject(flowTask);
        }
    }

    @ApiOperation(Translator.PREFIX + "i18n_swagger_task_log_query" + Translator.SUFFIX)
    @ApiHasModules("dashboard")
    @GetMapping(value = "/task/log/{processId}")
    @I18n(I18nConstants.CLUSTER)
    public List<FlowProcessLog> getTaskLog(@PathVariable String processId) {
        return taskService.listTaskLog(processId);
    }

    @GetMapping(value = "/stepAssignees/{modelId}/{step}")
    public List<User> getStepAssignees(@PathVariable String modelId, @PathVariable Integer step) {
        return taskService.getStepAssignees(null, modelId, step);
    }

    @GetMapping(value = "/stepAssignees/{processId}/{modelId}/{step}")
    public List<User> getStepAssignees(@PathVariable String processId, @PathVariable String modelId, @PathVariable Integer step) {
        return taskService.getStepAssignees(processId, modelId, step);
    }

    @GetMapping(value = "/beforeLinks/{taskId}")
    public List<FlowTask> getLinkBeforeTask(@PathVariable String taskId) {
        return taskService.getLinkBeforeTask(taskId);
    }

}
