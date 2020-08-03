package com.fit2cloud.commons.server.process;

import com.fit2cloud.commons.server.base.domain.FlowProcess;
import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.config.BeforeTest;
import com.fit2cloud.commons.server.constants.ProcessConstants;
import com.fit2cloud.commons.server.process.dto.ProcessDTO;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProcessTest extends BeforeTest {

    @Resource
    private ProcessMessageService processMessageService;

    @Resource
    private ProcessService processService;

    @Resource
    private TaskService taskService;

    @Test
    public void testTemplateService() {
        String template = "<p th:text='${user}'></p>";
        Map<String, Object> map = new HashMap<>();
        map.put("user", "mk");
        System.out.println(TemplateUtils.merge(template, map));
    }

    @Test
    public void testCreateProcess() {
        ProcessDTO processDTO = new ProcessDTO();
        processDTO.setCreator("admin@fit2cloud.com");
        processDTO.setBusinessKey("test unit");
        processDTO.setBusinessType("test");
        processDTO.setModelId("vm_apply");
        processDTO.setProcessName("测试流程");
        processDTO.setWorkspaceId("0f87fa52-c318-4eba-99b7-e062d2810c6e");
        processDTO.setProcessId("12345678901");
        FlowProcess flowProcess = processService.createProcess(processDTO);
        System.out.println(flowProcess.getProcessId());
    }

    @Test
    public void testListCurrentTask() {
        List<FlowTask> flowTasks = taskService.listCurrentTask("12345678901");
        flowTasks.forEach(task -> System.out.println(task.getTaskAssignee()));
    }

    @Test
    public void testCompleteStep1() {
        FlowTask task = taskService.getLastTaskByAssignee("12345678901", "dongbin@fit2cloud.com");
        taskService.complete(task, null);
    }

    @Test
    public void testCompleteStep2() {
        FlowTask task = taskService.getLastTaskByAssignee("12345678901", "admin@fit2cloud.com");
        taskService.complete(task, null);
    }

    @Test
    public void testSendMessage() {
        Map<String, Object> map = new HashMap<>();
        map.put("CREATOR", "admin@fit2cloud.com");
        map.put("CREATOR_NAME", "admin");
        map.put("PROCESS_ID", "12345678901");
        map.put("PROCESS_NAME", "测试流程");
        processMessageService.sendProcessMessage("order-92668751-20181016174146678", ProcessConstants.MessageOperation.COMPLETE, map);
    }

    @Test
    public void testGetNextStepAssignee() {
        System.out.println(new Gson().toJson(taskService.getStepAssignees(null, "vm_renew", 0)));
    }


}