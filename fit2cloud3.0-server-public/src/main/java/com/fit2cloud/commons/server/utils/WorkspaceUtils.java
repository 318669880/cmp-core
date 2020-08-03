package com.fit2cloud.commons.server.utils;

import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtWorkspaceCommonMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Author: chunxing
 * Date: 2018/6/1  下午2:08
 * Description:
 */
@Component
public class WorkspaceUtils {

    private static ExtWorkspaceCommonMapper extWorkspaceCommonMapper;

    private static WorkspaceMapper workspaceMapper;

    @Resource
    public void setExtWorkspaceCommonMapper(ExtWorkspaceCommonMapper extWorkspaceCommonMapper) {
        WorkspaceUtils.extWorkspaceCommonMapper = extWorkspaceCommonMapper;
    }

    @Resource
    public void settWorkspaceCommonMapper(WorkspaceMapper workspaceMapper) {
        WorkspaceUtils.workspaceMapper = workspaceMapper;
    }

    public static List<String> getWorkspaceIdsByOrgIds(String... Ids) {
        List<String> list = Arrays.asList(Ids);
        return extWorkspaceCommonMapper.getWorkspaceIdsByOrgIds(list);
    }

    public static List<String> getWorkspaceIdsByOrgIds(List<String> Ids) {
        return extWorkspaceCommonMapper.getWorkspaceIdsByOrgIds(Ids);
    }

    public static void resetWorkspace(String tableName, String fieldName, String workspaceId) {
        extWorkspaceCommonMapper.resetWorkspace(tableName, fieldName, workspaceId);
    }

    public static String getOrgIdByWorkspaceId(String workspaceId) {
        Workspace workspace = workspaceMapper.selectByPrimaryKey(workspaceId);
        if (workspace != null) {
            return workspace.getOrganizationId();
        }
        return null;
    }

}
