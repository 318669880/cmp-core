package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.model.TreeNode;
import com.fit2cloud.commons.server.model.WorkspaceOrganization;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Author: chunxing
 * Date: 2018/6/1  下午2:11
 * Description:
 */
public interface ExtWorkspaceCommonMapper {
    List<String> getWorkspaceIdsByOrgIds(@Param("ids") List<String> ids);

    void resetWorkspace(@Param("tableName") String tableName, @Param("fieldName") String fieldName, @Param("workspaceId") String workspaceId);

    WorkspaceOrganization getWorkspaceOrganizationByWorkspaceId(String id);

    List<TreeNode> selectWorkspaceTreeNode(@Param("userId") String userId, @Param("roleId") String roleId, @Param("organizationId") String organizationId);
}
