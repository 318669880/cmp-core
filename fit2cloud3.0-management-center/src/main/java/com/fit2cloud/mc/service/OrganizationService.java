package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import com.fit2cloud.commons.server.base.mapper.UserRoleMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.constants.ResourceTypeConstants;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.server.utils.UserRoleUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.MessageConstants;
import com.fit2cloud.mc.dao.ext.ExtOrganizationMapper;
import com.fit2cloud.mc.dao.ext.ExtWorkspaceMapper;
import com.fit2cloud.mc.dto.OrganizationDTO;
import com.fit2cloud.mc.dto.request.CreateOrganizationRequest;
import com.fit2cloud.mc.dto.request.UpdateOrganizationRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: chunxing
 * Date: 2018/5/22  下午7:20
 * Description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrganizationService {

    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private ExtOrganizationMapper extOrganizationMapper;
    @Resource
    private WorkspaceMapper workspaceMapper;
    @Resource
    private ExtWorkspaceMapper extWorkspaceMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserRoleMapper userRoleMapper;

    public Object organizations(SessionUser sessionUser) {
        OrganizationExample example = new OrganizationExample();
        if (StringUtils.equals(sessionUser.getParentRoleId(), RoleConstants.Id.ORGADMIN.name())) {
            Set<String> resourceIds = UserRoleUtils.getResourceIds(sessionUser.getId());
            example.createCriteria().andIdIn(new ArrayList<>(resourceIds));
        }
        example.setOrderByClause("name");
        return organizationMapper.selectByExample(example);
    }

    public List<OrganizationDTO> paging(Map<String, Object> map) {
        return extOrganizationMapper.paging(map);
    }

    public List<String> currentLineIds(List<String> orgIds){
        List<Map<String, String>> idMaps = extOrganizationMapper.ids();
        List<String> results = lineIds(orgIds, idMaps);
        return results;
    }

    public List<String> lineIds(List<String> orgIds, List<Map<String,String>> allIdMaps){
        Set<String> pNodeIdSets = new HashSet<>();
        Set<String> cNodeIdSets = new HashSet<>();
        List<Map<String, String>> currentIds = allIdMaps.stream().filter(idMap -> orgIds.contains(idMap.get("id"))).collect(Collectors.toList());


        Map<String, Map<String, String>> nodesMap = allIdMaps.stream().collect(Collectors.toMap(idMap -> idMap.get("id"), k -> k));
        Map<String, List<Map<String, String>>> pnodesMap = allIdMaps.stream().collect(Collectors.groupingBy(idMap -> Optional.ofNullable(idMap.get("pid")).orElse("-1")));
        //向上依次获取父节点ID
        currentIds.stream().forEach(idMap -> {
            while (ObjectUtils.isNotEmpty(idMap)) {
                pNodeIdSets.add(idMap.get("id"));
                String parentId = idMap.get("pid");
                idMap = StringUtils.isNotEmpty(parentId) ? nodesMap.get(parentId) : null;
            }
        });

        while (CollectionUtils.isNotEmpty(currentIds)){
            List<Map<String, String>> kids = new ArrayList<>();
            for (int i = 0; i < currentIds.size(); i++) {
                Map<String, String> idMap = currentIds.get(i);
                String id = idMap.get("id");
                cNodeIdSets.add(id);
                Optional.ofNullable(pnodesMap.get(id)).ifPresent(kids::addAll);
            }
            currentIds = kids;
        }

        pNodeIdSets.addAll(cNodeIdSets);

        return new ArrayList<>(pNodeIdSets);
    }



    public List<String> currentOrganizationByName(String name){
        OrganizationExample example = new OrganizationExample();
        example.createCriteria().andNameLike(name);
        List<Organization> organizations = organizationMapper.selectByExample(example);
        return organizations.stream().map(Organization::getId).collect(Collectors.toList());
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(List<String> organizationIds) {
        // is has linked workspaces?
        WorkspaceExample countExample = new WorkspaceExample();
        countExample.createCriteria().andOrganizationIdIn(organizationIds);
        long countWorkspace = workspaceMapper.countByExample(countExample);
        if (countWorkspace > 0) {
            throw new RuntimeException(Translator.get("i18n_ex_organization_delete_workspace"));
        }
        organizationIds.forEach(organizationId -> {
            organizationMapper.deleteByPrimaryKey(organizationId);
            //删除和用户相关联的组织
            UserRoleExample example = new UserRoleExample();
            example.createCriteria().andSourceIdEqualTo(organizationId);
            userRoleMapper.deleteByExample(example);
            OperationLogService.log(null, organizationId, null, ResourceTypeConstants.ORGANIZATION.name(), ResourceOperation.DELETE, null);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public Organization insert(CreateOrganizationRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            F2CException.throwException(Translator.get("i18n_ex_organization_name"));
        }

        Organization organization = new Organization();
        BeanUtils.copyBean(organization, request);
        organization.setId(UUIDUtil.newUUID());
        organization.setCreateTime(Instant.now().toEpochMilli());
        organization.setLevel(0);
        Optional.ofNullable(request.getPid()).ifPresent(pid -> {
            Organization parentOrganization = organizationMapper.selectByPrimaryKey(pid);
            Integer pLevel = ObjectUtils.isEmpty(parentOrganization) ? -1 : parentOrganization.getLevel();
            organization.setLevel(pLevel + 1);
        });
        try {
            organizationMapper.insert(organization);
            OperationLogService.log(null, organization.getId(), organization.getName(), ResourceTypeConstants.ORGANIZATION.name(), ResourceOperation.CREATE, null);
        } catch (DuplicateKeyException e) {
            F2CException.throwException(MessageConstants.NameDuplicateKey);
        }

        return organization;
    }

    @Transactional(rollbackFor = Exception.class)
    public Organization update(UpdateOrganizationRequest request) {
        if (StringUtils.isBlank(request.getId())) {
            F2CException.throwException(Translator.get("i18n_ex_organization_id"));
        }
        if (StringUtils.isBlank(request.getName())) {
            F2CException.throwException(Translator.get("i18n_ex_organization_name"));
        }
        Organization organization = new Organization();
        BeanUtils.copyBean(organization, request);
        organization.setLevel(0);
        Optional.ofNullable(request.getPid()).ifPresent(pid -> {
            Organization parentOrganization = organizationMapper.selectByPrimaryKey(pid);
            Integer pLevel = ObjectUtils.isEmpty(parentOrganization) ? -1 : parentOrganization.getLevel();
            organization.setLevel(pLevel + 1);
        });
        try {
            //organizationMapper.updateByPrimaryKeySelective(organization);
            extOrganizationMapper.updateOrganization(organization);
            OperationLogService.log(null, organization.getId(), organization.getName(), ResourceTypeConstants.ORGANIZATION.name(), ResourceOperation.UPDATE, null);
        } catch (DuplicateKeyException e) {
            F2CException.throwException(MessageConstants.NameDuplicateKey);
        }
        return organization;
    }

    public List<Workspace> linkWorkspacePaging(String organizationId) {
        return extWorkspaceMapper.linkWorkspacePaging(organizationId);
    }

    public List<UserDTO> linkOrgAdminPaging(String organizationId) {
        List<UserDTO> list = extWorkspaceMapper.linkOrgAdminPaging(organizationId);
        userService.convertUserDTO(list, new HashMap<>());
        return list;
    }

    public Object currentOrganization(String organizationId) {
        Organization organization = organizationMapper.selectByPrimaryKey(organizationId);
        return Collections.singletonList(organization);
    }
}
