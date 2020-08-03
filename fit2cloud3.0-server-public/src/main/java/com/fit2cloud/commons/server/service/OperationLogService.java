package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.OperationLogMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.constants.SystemUserConstants;
import com.fit2cloud.commons.server.constants.WorkspaceConstants;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.module.ServerInfo;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.sdk.model.F2CEntityType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class OperationLogService {

    private static OperationLogMapper operationLogMapper;
    private static ServerInfo serverInfo;
    private static WorkspaceMapper workspaceMapper;

    public static void log(Workspace owner, String resourceId, String resourceName, String resourceType, String operation, String message) {
        User user = SessionUtils.getUser();
        String ip = SessionUtils.getRemoteAddress();
        OperationLog operationLog = createOperationLog(owner, user, resourceId, resourceName, resourceType, operation, message, ip);
        operationLogMapper.insert(operationLog);
    }

    public static void log(Workspace owner, User user, String resourceId, String resourceName, String resourceType, String operation, String message) {
        String ip = SessionUtils.getRemoteAddress();
        OperationLog operationLog = createOperationLog(owner, user, resourceId, resourceName, resourceType, operation, message, ip);
        operationLogMapper.insert(operationLog);
    }

    public static void log(OperationLog operationLog) {
        if (StringUtils.isBlank(operationLog.getId())) {
            operationLog.setId(UUIDUtil.newUUID());
        }
        operationLogMapper.insert(operationLog);
    }

    public static void log(CloudServer cloudServer, String operation, String message) {
        User user = SessionUtils.getUser();
        Workspace workspace = workspaceMapper.selectByPrimaryKey(cloudServer.getWorkspaceId());
        log(workspace, user, cloudServer.getId(), cloudServer.getInstanceName(), F2CEntityType.VIRTUALMACHINE.name(), operation, message);
    }

    public static void log(CloudDisk cloudDisk, String operation, String message) {
        User user = SessionUtils.getUser();
        Workspace workspace = workspaceMapper.selectByPrimaryKey(cloudDisk.getWorkspaceId());
        log(workspace, user, cloudDisk.getId(), cloudDisk.getDiskName(), F2CEntityType.DISK.name(), operation, message);
    }

    public static OperationLog createOperationLog(Workspace workspace, User user, String resourceId, String resourceName, String resourceType, String operation, String message, String ip) {
        OperationLog operationLog = new OperationLog();
        operationLog.setId(UUIDUtil.newUUID());
        operationLog.setResourceId(resourceId);
        operationLog.setResourceName(resourceName);
        if (workspace == null) {
            operationLog.setWorkspaceId(WorkspaceConstants.ROOT);
            operationLog.setWorkspaceName(WorkspaceConstants.SYSTEM);
        } else {
            operationLog.setWorkspaceId(workspace.getId());
            operationLog.setWorkspaceName(workspace.getName());
        }
        if (user == null) {
            operationLog.setResourceUserId(SystemUserConstants.getUserId());
            operationLog.setResourceUserName(SystemUserConstants.getUser().getName());
        } else {
            operationLog.setResourceUserId(user.getId());
            operationLog.setResourceUserName(user.getName() + " [" + user.getEmail() + "]");
        }
        operationLog.setResourceType(resourceType);
        operationLog.setOperation(operation);
        operationLog.setMessage(message);
        operationLog.setModule(serverInfo.getModule().getId());
        operationLog.setSourceIp(ip);
        operationLog.setTime(System.currentTimeMillis());
        return operationLog;
    }

    @Autowired
    public void setWorkspaceMapper(WorkspaceMapper workspaceMapper) {
        OperationLogService.workspaceMapper = workspaceMapper;
    }

    @Autowired
    public void setOperationLogMapper(OperationLogMapper operationLogMapper) {
        OperationLogService.operationLogMapper = operationLogMapper;
    }

    @Autowired
    public void setServerInfo(ServerInfo serverInfo) {
        OperationLogService.serverInfo = serverInfo;
    }

    public List<OperationLog> selectRersourceOperationLog(String resourceId) {
        OperationLogExample example = new OperationLogExample();
        example.createCriteria().andResourceIdEqualTo(resourceId);
        example.setOrderByClause("time desc");
        return operationLogMapper.selectByExample(example);
    }

    public List<OperationLog> selectUserOperationLog(String userId) {
        OperationLogExample example = new OperationLogExample();
        example.createCriteria().andResourceUserIdEqualTo(userId);
        example.setOrderByClause("time desc");
        return operationLogMapper.selectByExample(example);
    }

    public List<OperationLog> selectWorkspaceOperationLog(String workspaceId) {
        OperationLogExample example = new OperationLogExample();
        example.createCriteria().andWorkspaceIdEqualTo(workspaceId);
        example.setOrderByClause("time desc");
        return operationLogMapper.selectByExample(example);
    }
}
