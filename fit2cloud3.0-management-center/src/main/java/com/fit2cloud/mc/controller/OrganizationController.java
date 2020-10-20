package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.OrgTreeNode;
import com.fit2cloud.commons.server.model.OrgTreeQueryDto;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.request.CreateOrganizationRequest;
import com.fit2cloud.mc.dto.request.DeleteOrgTreeRequest;
import com.fit2cloud.mc.dto.request.UpdateOrganizationRequest;
import com.fit2cloud.mc.service.OrganizationService;
import com.fit2cloud.mc.service.WorkspaceService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/organization")
@RestController
@Api(tags = Translator.PREFIX + "i18n_mc_organization_tag" + Translator.SUFFIX)
public class OrganizationController {

    @Resource
    private OrganizationService organizationService;

    @Resource
    private WorkspaceService workspaceService;

    @Resource
    private UserCommonService userCommonService;


    @RequiresPermissions(value = {PermissionConstants.USER_READ,
            PermissionConstants.WORKSPACE_EDIT,
            PermissionConstants.WORKSPACE_CREATE,}, logical = Logical.OR)
    @RequestMapping(method = RequestMethod.GET)
    public Object organizations() {
        SessionUser sessionUser = SessionUtils.getUser();
        return organizationService.organizations(sessionUser);
    }

    @GetMapping("/currentOrganization")
    public Object currentOrganization() {
        SessionUser sessionUser = SessionUtils.getUser();
        return organizationService.currentOrganization(sessionUser.getOrganizationId());
    }

    /*@ApiOperation(value = Translator.PREFIX + "i18n_mc_organization_list" + Translator.SUFFIX)
    @PostMapping(value = "/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.ORGANIZATION_READ)
    public Pager<List<OrganizationDTO>> paging(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody OrganizationRequest request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, organizationService.paging(BeanUtils.objectToMap(request)));
    }*/

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_organization_list" + Translator.SUFFIX)
    @PostMapping("/query")
    public List<OrgTreeNode> orgTree(@RequestBody OrgTreeQueryDto orgTreeQueryDto){
        return userCommonService.orgTreeNodeList(orgTreeQueryDto.getRootId(), orgTreeQueryDto.getOrgName(), orgTreeQueryDto.getExcludeWs());
    }

    @RequiresPermissions(PermissionConstants.ROLE_READ)
    @PostMapping(value = "link/workspace/{organizationId}/{goPage}/{pageSize}")
    public Pager<List<Workspace>> linkWorkspacePaging(@PathVariable String organizationId, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, organizationService.linkWorkspacePaging(organizationId));
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_organization_get_org_admin" + Translator.SUFFIX)
    @RequiresPermissions(PermissionConstants.ROLE_READ)
    @PostMapping(value = "user/{organizationId}/{goPage}/{pageSize}")
    public Pager<List<UserDTO>> linkOrgAdminPaging(@PathVariable String organizationId, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, organizationService.linkOrgAdminPaging(organizationId));
    }

    /*@ApiOperation(value = Translator.PREFIX + "i18n_mc_organization_delete_batch" + Translator.SUFFIX)
    @PostMapping(value = "/delete")
    @RequiresPermissions(PermissionConstants.ORGANIZATION_DELETE)
    public void delete(@RequestBody List<String> organizationIds) {
        organizationService.delete(organizationIds);
    }*/

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_organization_create" + Translator.SUFFIX)
    @PostMapping("/add")
    @RequiresPermissions(PermissionConstants.ORGANIZATION_CREATE)
    public Organization insert(@RequestBody CreateOrganizationRequest request) {
        return organizationService.insert(request);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_organization_edit" + Translator.SUFFIX)
    @PostMapping("/update")
    @RequiresPermissions(PermissionConstants.ORGANIZATION_EDIT)
    public Organization update(@RequestBody UpdateOrganizationRequest request) {
        return organizationService.update(request);
    }


    @ApiOperation(value = Translator.PREFIX + "i18n_mc_organization_delete_batch" + Translator.SUFFIX)
    @PostMapping(value = "/deleteTree")
    @RequiresPermissions(PermissionConstants.ORGANIZATION_DELETE)
    @Transactional
    public void deleteTree(@RequestBody List<DeleteOrgTreeRequest> deleteNodes){
        Map<String,List<DeleteOrgTreeRequest>> idsMap = deleteNodes.stream().collect(Collectors.groupingBy(DeleteOrgTreeRequest::getNodeType));

        Optional.ofNullable(idsMap.get("wks")).ifPresent(wksNodes -> {
            wksNodes.forEach(wkNode -> workspaceService.delete(wkNode.getNodeId()));
        });

        Optional.ofNullable(idsMap.get("org")).ifPresent(orgNodes -> {
            organizationService.delete(orgNodes.stream().map(DeleteOrgTreeRequest::getNodeId).collect(Collectors.toList()));
        });
    }

}
