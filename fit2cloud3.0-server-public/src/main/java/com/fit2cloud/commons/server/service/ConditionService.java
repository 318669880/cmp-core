package com.fit2cloud.commons.server.service;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.server.base.domain.UserExample;
import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.base.domain.WorkspaceExample;
import com.fit2cloud.commons.server.base.mapper.CloudAccountMapper;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtCloudAccountMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtUserCommonMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtWorkspaceCommonMapper;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.constants.SystemUserConstants;
import com.fit2cloud.commons.server.model.CloudAccountDTO;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.model.UserTooltip;
import com.fit2cloud.commons.server.model.WorkspaceOrganization;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConditionService {

    @Resource
    private WorkspaceMapper workspaceMapper;
    @Resource
    private ExtCloudAccountMapper extCloudAccountMapper;
    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private ExtUserCommonMapper extUserCommonMapper;
    @Resource
    private ExtWorkspaceCommonMapper extWorkspaceCommonMapper;

    /**
     * 获取工作空间筛选列表
     *
     * @return
     */
    public List<Workspace> getWorkspaceList(String organizationId) {


        WorkspaceExample workspaceExample = new WorkspaceExample();
        WorkspaceExample.Criteria criteria = workspaceExample.createCriteria();

        List<Workspace> workspaces = new ArrayList<>();
        if (RoleConstants.Id.ADMIN.name().equals(SessionUtils.getUser().getParentRoleId())) {
            workspaces = workspaceMapper.selectByExample(workspaceExample);
        } else if (RoleConstants.Id.ORGADMIN.name().equals(SessionUtils.getUser().getParentRoleId())) {
            if (!StringUtils.isBlank(organizationId)) {
                criteria.andOrganizationIdEqualTo(organizationId);
            } else {
                criteria.andOrganizationIdEqualTo(SessionUtils.getUser().getOrganizationId());
            }
            workspaces = workspaceMapper.selectByExample(workspaceExample);
        }
        return workspaces;
    }

    /**
     * 获取云账号列表(基础设施和容器云)
     *
     * @param pluginConstants
     * @return List<CloudAccount>
     */
    public List<CloudAccountDTO> getCloudAccountList(String pluginConstants) {
        if (RoleConstants.Id.ADMIN.name().equals(SessionUtils.getUser().getParentRoleId())) {
            Map<String, Object> param = new HashMap<>();
            param.put("pluginType", pluginConstants);
            return extCloudAccountMapper.getAccountList(param);
        }
        return new ArrayList<>();
    }

    public List<Organization> getOrganizationList() {
        List<Organization> organizations = new ArrayList<>();
        if (RoleConstants.Id.ADMIN.name().equals(SessionUtils.getUser().getParentRoleId())) {
            organizations = organizationMapper.selectByExample(null);
        }
        return organizations;
    }

    public void convertParam(Map<String, Object> param) {
        SessionUser user = SessionUtils.getUser();
        if (StringUtils.equals(user.getParentRoleId(), RoleConstants.Id.ORGADMIN.name())) {
            String organizationId = user.getOrganizationId();
            WorkspaceExample workspaceExample = new WorkspaceExample();
            workspaceExample.createCriteria().andOrganizationIdEqualTo(organizationId);
            List<Workspace> workspaces = workspaceMapper.selectByExample(workspaceExample);
            List<String> list = workspaces.stream().map(Workspace::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)) {
                //避免没有工作空间 组织管理员查询全部
                list.add(UUIDUtil.newUUID());
            }
            param.put("workspaceIds", list);
        } else if (StringUtils.equals(user.getParentRoleId(), RoleConstants.Id.USER.name())) {
            param.put("workspaceId", user.getWorkspaceId());
        }
    }

    public List<UserTooltip> getUserTooltipByIds(List<String> ids) {
        List<UserTooltip> result = new ArrayList<>();

        if (CollectionUtils.isEmpty(ids)) {
            return result;
        }
        List<UserTooltip> userList = extUserCommonMapper.getUserTooltip(ids);
        Map<String, UserTooltip> userTooltipMap = new HashMap<>();
        userList.forEach(userTooltip -> userTooltipMap.put(userTooltip.getId(), userTooltip));

        ids.forEach(id -> {
            if (userTooltipMap.get(id) == null) {
                if (SystemUserConstants.getUserId().equalsIgnoreCase(id)) {
                    UserTooltip userTooltip = new UserTooltip();
                    userTooltip.setId(SystemUserConstants.getUserId());
                    userTooltip.setName(SystemUserConstants.getUser().getName());
                    userTooltip.setEmail("");
                    result.add(userTooltip);
                } else {
                    result.add(new UserTooltip(id, false));
                }
            } else {
                result.add(userTooltipMap.get(id));
            }
        });

        return result;
    }

    public List<UserTooltip> searchUser(String keyWord) {
        List<UserTooltip> result = new ArrayList<>();
        if (StringUtils.isBlank(keyWord)) {
            return result;
        }
        if (StringUtils.containsIgnoreCase(SystemUserConstants.getUserId(), keyWord)) {
            UserTooltip userTooltip = new UserTooltip();
            userTooltip.setId(SystemUserConstants.getUserId());
            userTooltip.setName(SystemUserConstants.getUser().getName());
        }
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdLike(keyWord).andNameLike(keyWord).andEmailLike(keyWord);
        result.addAll(extUserCommonMapper.searchUser(userExample));
        return result;
    }


    public Map<String, String> getCloudAccountNameById(String id) {

        Map<String, String> map = new HashMap<>();

        List<CloudAccountDTO> accountList = extCloudAccountMapper.getAccountList(ImmutableMap.of("id", id));

        if (!CollectionUtils.isEmpty(accountList) && accountList.size() == 1) {
            CloudAccountDTO accountDTO = accountList.get(0);
            map.put("accountId", id);
            map.put("accountName", accountDTO.getName());
            map.put("accountStatus", accountDTO.getStatus());
            try {
                map.put("webConsole", JSON.parseObject(accountDTO.getCredential()).getString("WebConsole"));
            } catch (Exception e) {
                // not to do
            }
            map.put("icon", accountDTO.getIcon());
        }

        return map;
    }

    public String getOrganizationNameById(String id) {

        Organization organization = organizationMapper.selectByPrimaryKey(id);

        if (organization != null) {
            return organization.getName();
        }

        return "";
    }

    public WorkspaceOrganization getWorkspaceOrganizationByWorkspaceId(String id) {
        return extWorkspaceCommonMapper.getWorkspaceOrganizationByWorkspaceId(id);
    }
}
