package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.Role;
import com.fit2cloud.commons.server.base.domain.UserRole;
import com.fit2cloud.commons.server.base.domain.UserRoleExample;
import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.base.domain.WorkspaceExample;
import com.fit2cloud.commons.server.base.mapper.RoleMapper;
import com.fit2cloud.commons.server.base.mapper.UserRoleMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtRoleCommonMapper;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.utils.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: chunxing
 * Date: 2018/5/9  上午10:49
 * Description:
 */
@Service
public class RoleCommonService {

    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private ExtRoleCommonMapper extRoleCommonMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private WorkspaceMapper workspaceMapper;

    public List<Role> listSystemRoles() {
        return roleMapper.selectByExample(null);
    }

    public RoleConstants.Id getCurrentRoleId() {
        String roleId = SessionUtils.getUser().getParentRoleId();
        if (isExtendAdmin(roleId)) {
            return RoleConstants.Id.ADMIN;
        }

        if (isExtendOrgAdmin(roleId)) {
            return RoleConstants.Id.ORGADMIN;
        }

        return RoleConstants.Id.USER;
    }

    public List<String> listAdmins(String roleId) {
        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        return userRoleMapper.selectByExample(example).stream().map(UserRole::getUserId).collect(Collectors.toList());
    }

    public List<String> listOrgAdmins(String roleId, String sourceId) {
        // 如果是工作空间ID，则获取组织ID
        Workspace workspace = workspaceMapper.selectByPrimaryKey(sourceId);
        sourceId = workspace == null ? sourceId : workspace.getOrganizationId();

        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andRoleIdEqualTo(roleId).andSourceIdEqualTo(sourceId);
        return userRoleMapper.selectByExample(example).stream().map(UserRole::getUserId).collect(Collectors.toList());
    }

    public List<String> listUsers(String roleId, String sourceId) {
        //如果sourceId是组织ID，则获取所有工作空间ID
        WorkspaceExample workspaceExample = new WorkspaceExample();
        workspaceExample.createCriteria().andOrganizationIdEqualTo(sourceId);
        List<Workspace> workspaces = workspaceMapper.selectByExample(workspaceExample);

        List<String> workspaceIds = new ArrayList<>();
        if (workspaces.size() > 0) {
            workspaceIds.addAll(workspaces.stream().map(Workspace::getId).collect(Collectors.toList()));
        } else {
            workspaceIds.add(sourceId);
        }

        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andRoleIdEqualTo(roleId).andSourceIdIn(workspaceIds);
        return userRoleMapper.selectByExample(example).stream().map(UserRole::getUserId).collect(Collectors.toList());
    }

    public Set<String> getRoleNamesByUserId(String userId) {
        return extRoleCommonMapper.getRoleNamesByUserId(userId);
    }

    public String getRoleIdByUserRoleId(String userRoleId) {
        return userRoleMapper.selectByPrimaryKey(userRoleId).getRoleId();
    }

    public String getParentId(String roleId) {
        UserRole userRole = userRoleMapper.selectByPrimaryKey(roleId);
        Role role = roleMapper.selectByPrimaryKey(userRole.getRoleId());
        if (role != null) {
            String parentId = role.getParentId();
            if (StringUtils.isNotEmpty(parentId)) {
                return parentId;
            } else {
                return role.getId();
            }
        }
        return null;
    }

    public String getSourceIdByUserRoleId(String userRoleId) {
        return Optional.ofNullable(userRoleMapper.selectByPrimaryKey(userRoleId)).orElse(new UserRole()).getSourceId();
    }

    public boolean isExtendAdmin(String roleId) {
        if (StringUtils.equals(roleId, RoleConstants.Id.ADMIN.name())) {
            return true;
        }
        Role role = roleMapper.selectByPrimaryKey(roleId);
        return StringUtils.equals(role.getParentId(), RoleConstants.Id.ADMIN.name());
    }

    public boolean isExtendOrgAdmin(String roleId) {
        if (StringUtils.equals(roleId, RoleConstants.Id.ORGADMIN.name())) {
            return true;
        }
        Role role = roleMapper.selectByPrimaryKey(roleId);
        return StringUtils.equals(role.getParentId(), RoleConstants.Id.ORGADMIN.name());
    }

    public boolean isExtendUser(String roleId) {
        if (StringUtils.equals(roleId, RoleConstants.Id.USER.name())) {
            return true;
        }
        Role role = roleMapper.selectByPrimaryKey(roleId);
        return StringUtils.equals(role.getParentId(), RoleConstants.Id.USER.name());
    }

    /**
     * 当前用户是否为系统管理员
     *
     * @return
     */
    public boolean isAdmin() {
        try {
            return RoleConstants.Id.ADMIN.name().equals(SessionUtils.getUser().getParentRoleId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 当前用户是否为组织管理员
     *
     * @return
     */
    public boolean isOrgAdmin() {
        try {
            return RoleConstants.Id.ORGADMIN.name().equals(SessionUtils.getUser().getParentRoleId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 当前用户是否为工作空间用户
     *
     * @return
     */
    public boolean isUser() {
        try {
            return RoleConstants.Id.USER.name().equals(SessionUtils.getUser().getParentRoleId());
        } catch (Exception e) {
            return false;
        }
    }

}
