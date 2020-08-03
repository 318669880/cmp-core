package com.fit2cloud.commons.server;

import com.fit2cloud.commons.server.base.domain.OperationLog;
import com.fit2cloud.commons.server.config.BeforeTest;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.server.service.ParameterCommonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OperationLogTest extends BeforeTest {
    @Resource
    private OperationLogService operationLogService;

    @Resource
    private ParameterCommonService parameterCommonService;

    @Test
    public void testQueryUserLog() {
        List<OperationLog> operationLogs = operationLogService.selectUserOperationLog("admin@fit2cloud.com");
        System.out.println(operationLogs.size());
    }

    @Test
    public void testQueryResourceLog() {
        List<OperationLog> operationLogs = operationLogService.selectRersourceOperationLog("09da35ec-9851-497d-a577-2b83591e4787");
        System.out.println(operationLogs.size());
    }

    @Test
    public void testQueryWorkspaceLog() {
        List<OperationLog> operationLogs = operationLogService.selectWorkspaceOperationLog("e46ffe97-f2e6-43c2-951b-b4adffc40346");
        System.out.println(operationLogs.size());
    }

    @Test
    public void uiInfo() {
        Object uiInfo = parameterCommonService.uiInfo();
    }
}
