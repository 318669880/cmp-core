package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.server.utils.WorkspaceUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.WorkspaceDTO;
import com.fit2cloud.mc.dto.request.CreateWorkspaceRequest;
import com.fit2cloud.mc.dto.request.UpdateWorkspaceRequest;
import com.fit2cloud.mc.dto.request.WorkspaceRequest;
import com.fit2cloud.mc.service.WorkspaceService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * Author: chunxing
 * Date: 2018/5/22  下午7:16
 * Description:
 */
@Api(tags = Translator.PREFIX + "i18n_mc_workspace_tag" + Translator.SUFFIX)
@RequestMapping("/workspace")
@RestController
public class WorkspaceController {

    @Resource
    private WorkspaceService workspaceService;

    /**
     * admin new user use
     *
     * @param orgId
     * @return List<Workspace>
     */
    @RequestMapping(value = "org/{orgId}", method = RequestMethod.GET)
    public List<Workspace> workspacesByOrgId(@PathVariable String orgId) {
        return workspaceService.workspacesByOrgId(orgId);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_workspace_list" + Translator.SUFFIX)
    @PostMapping(value = "/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.WORKSPACE_READ)
    @I18n
    public Pager<List<WorkspaceDTO>> paging(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody WorkspaceRequest request) {
        Map<String, Object> map = BeanUtils.objectToMap(request);
        if (StringUtils.equals(SessionUtils.getUser().getParentRoleId(), RoleConstants.Id.ORGADMIN.name())) {
            map.put("role", RoleConstants.Id.ORGADMIN.name());
            List<String> resourceIds = WorkspaceUtils.getWorkspaceIdsByOrgIds(SessionUtils.getOrganizationId());
            resourceIds.add(UUIDUtil.newUUID());
            map.put("resourceIds", resourceIds);
        }
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, workspaceService.paging(map));
    }

    @RequestMapping(method = RequestMethod.GET)
    @I18n
    public List<Workspace> workspaces() {
        return workspaceService.workspaces();
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_workspace_create" + Translator.SUFFIX)
    @PostMapping("/add")
    @RequiresPermissions(PermissionConstants.WORKSPACE_CREATE)
    @I18n
    public WorkspaceDTO insert(@RequestBody CreateWorkspaceRequest request) {
        return workspaceService.insert(request);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_workspace_edit" + Translator.SUFFIX)
    @PostMapping("/update")
    @RequiresPermissions(PermissionConstants.WORKSPACE_EDIT)
    @I18n
    public WorkspaceDTO update(@RequestBody UpdateWorkspaceRequest request) {
        return workspaceService.update(request);
    }

    /*@ApiOperation(value = Translator.PREFIX + "i18n_mc_workspace_delete" + Translator.SUFFIX)
    @GetMapping(value = "/delete/{workspaceId}")
    @RequiresPermissions(PermissionConstants.WORKSPACE_DELETE)
    public void delete(@PathVariable String workspaceId) {
        workspaceService.delete(workspaceId);
    }*/

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_workspace_delete" + Translator.SUFFIX)
    @PostMapping(value = "/delete/{workspaceId}")
    @RequiresPermissions(PermissionConstants.WORKSPACE_DELETE)
    public void delete(@PathVariable String workspaceId) {
        workspaceService.delete(workspaceId);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_workspace_get_user" + Translator.SUFFIX)
    @RequiresPermissions(PermissionConstants.WORKSPACE_READ)
    @PostMapping(value = "user/{workspaceId}/{goPage}/{pageSize}")
    @I18n
    public Pager<List<UserDTO>> authorizeUsersPaging(@PathVariable String workspaceId, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, workspaceService.authorizeUsersPaging(workspaceId));
    }
}
