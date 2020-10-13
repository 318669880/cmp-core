package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.server.base.domain.UserRoleExample;
import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.base.domain.WorkspaceExample;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import com.fit2cloud.commons.server.base.mapper.UserRoleMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.constants.RedisConstants;
import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.constants.ResourceTypeConstants;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.redis.subscribe.RedisMessagePublisher;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.dao.ext.ExtWorkspaceMapper;
import com.fit2cloud.mc.dto.WorkspaceDTO;
import com.fit2cloud.mc.dto.request.CreateWorkspaceRequest;
import com.fit2cloud.mc.dto.request.UpdateWorkspaceRequest;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: chunxing
 * Date: 2018/5/22  下午7:17
 * Description:
 */
@Service
public class WorkspaceService {

    @Resource
    private WorkspaceMapper workspaceMapper;
    @Resource
    private ExtWorkspaceMapper extWorkspaceMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private UserService userService;
    @Resource
    private RedisMessagePublisher redisMessagePublisher;

    public List<Workspace> workspacesByOrgId(String orgId) {
        WorkspaceExample workspaceExample = new WorkspaceExample();
        workspaceExample.createCriteria().andOrganizationIdEqualTo(orgId);
        return workspaceMapper.selectByExample(workspaceExample);
    }

    public List<Workspace> workspaces() {
        WorkspaceExample example = new WorkspaceExample();
        if (StringUtils.equalsIgnoreCase(SessionUtils.getUser().getParentRoleId(), RoleConstants.Id.ORGADMIN.name())) {
            example.createCriteria().andOrganizationIdEqualTo(SessionUtils.getOrganizationId());
        }
        example.setOrderByClause("name");
        return workspaceMapper.selectByExample(example);
    }

    @Transactional(rollbackFor = Exception.class)
    public WorkspaceDTO insert(CreateWorkspaceRequest request) {

        if (StringUtils.isBlank(request.getName())) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_name_no_empty"));
        }

        if (StringUtils.equalsIgnoreCase(SessionUtils.getUser().getParentRoleId(), RoleConstants.Id.ADMIN.name())
                && StringUtils.isBlank(request.getOrganizationId())) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_orgId_no_empty"));
        }

        Organization organization = organizationMapper.selectByPrimaryKey(request.getOrganizationId());
        if (organization == null && StringUtils.equalsIgnoreCase(SessionUtils.getUser().getParentRoleId(), RoleConstants.Id.ADMIN.name())) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_org_no_exist"));
        }

        //判断名称是否存在
        WorkspaceExample workspaceExample = new WorkspaceExample();
        workspaceExample.createCriteria().andNameEqualTo(request.getName());

        List<Workspace> workspaces = workspaceMapper.selectByExample(workspaceExample);
        if (!CollectionUtils.isEmpty(workspaces)) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_name_exist"));
        }

        Workspace workspace = new Workspace();
        BeanUtils.copyBean(workspace, request);

        String workspaceId = UUIDUtil.newUUID();
        workspace.setId(workspaceId);
        workspace.setCreateTime(Instant.now().toEpochMilli());
        if (StringUtils.equalsIgnoreCase(SessionUtils.getUser().getParentRoleId(), RoleConstants.Id.ORGADMIN.name())) {
            workspace.setOrganizationId(SessionUtils.getOrganizationId());
        }
        workspaceMapper.insert(workspace);

        OperationLogService.log(null, workspace.getId(), workspace.getName(), ResourceTypeConstants.WORKSPACE.name(), ResourceOperation.CREATE, null);

        return getWorkspaceDTOById(workspaceId);
    }

    @Transactional(rollbackFor = Exception.class)
    public WorkspaceDTO update(UpdateWorkspaceRequest request) {

        if (StringUtils.isBlank(request.getId())) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_id_no_empty"));
        }

        if (StringUtils.isBlank(request.getName())) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_name_no_empty"));
        }

        if (StringUtils.equalsIgnoreCase(SessionUtils.getUser().getParentRoleId(), RoleConstants.Id.ADMIN.name())
                && StringUtils.isBlank(request.getOrganizationId())) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_orgId_no_empty"));
        }

        Organization organization = organizationMapper.selectByPrimaryKey(request.getOrganizationId());
        if (organization == null) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_org_no_exist"));
        }

        //判断名称是否存在
        WorkspaceExample workspaceExample = new WorkspaceExample();
        workspaceExample.createCriteria().andNameEqualTo(request.getName()).andIdNotEqualTo(request.getId());

        List<Workspace> workspaces = workspaceMapper.selectByExample(workspaceExample);
        if (!CollectionUtils.isEmpty(workspaces)) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_name_exist"));
        }

        if (workspaceMapper.selectByPrimaryKey(request.getId()) == null) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_id_no_exist"));
        }

        Workspace workspace = new Workspace();
        BeanUtils.copyBean(workspace, request);

        workspaceMapper.updateByPrimaryKeySelective(workspace);
        OperationLogService.log(null, workspace.getId(), workspace.getName(), ResourceTypeConstants.WORKSPACE.name(), ResourceOperation.UPDATE, null);
        return getWorkspaceDTOById(workspace.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String workspaceId) {
        Workspace workspace = workspaceMapper.selectByPrimaryKey(workspaceId);
        if (workspace == null) {
            F2CException.throwException(Translator.get("i18n_ex_workspace_no_exist") + workspaceId);
        }
        // delete be linked user_role
        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andSourceIdEqualTo(workspaceId);
        userRoleMapper.deleteByExample(example);
        workspaceMapper.deleteByPrimaryKey(workspaceId);
        OperationLogService.log(null, workspaceId, workspace.getName(), ResourceTypeConstants.WORKSPACE.name(), ResourceOperation.DELETE, null);
        redisMessagePublisher.publish(RedisConstants.Topic.WORKSPACE_DELETE, workspaceId);

    }

    private WorkspaceDTO getWorkspaceDTOById(String id) {
        List<WorkspaceDTO> workspaces = paging(ImmutableMap.of("id", id));
        if (!CollectionUtils.isEmpty(workspaces)) {
            return workspaces.get(0);
        }

        return null;
    }

    public List<WorkspaceDTO> paging(Map<String, Object> map) {
        return extWorkspaceMapper.paging(map);
    }

    public Workspace getWorkspaceById(String id) {
        return workspaceMapper.selectByPrimaryKey(id);
    }

    public List<UserDTO> authorizeUsersPaging(String workspaceId) {
        List<UserDTO> list = extWorkspaceMapper.authorizeUsersPaging(workspaceId);
        userService.convertUserDTO(list, new HashMap<>());
        return list;
    }

    public long countWorkSpace(){
        return workspaceMapper.countByExample(null);
    }

}
