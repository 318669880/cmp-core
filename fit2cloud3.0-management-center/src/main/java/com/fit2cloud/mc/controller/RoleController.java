package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.Role;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.TreeNode;
import com.fit2cloud.commons.server.service.MenuService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.RoleDTO;
import com.fit2cloud.mc.dto.RoleOperate;
import com.fit2cloud.mc.dto.request.RoleRequest;
import com.fit2cloud.mc.model.McTreeNode;
import com.fit2cloud.mc.service.RoleService;
import com.fit2cloud.mc.utils.RoleUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@RequestMapping("role")
@RestController
@Api(tags = Translator.PREFIX + "i18n_mc_role_tag" + Translator.SUFFIX)
public class RoleController {

    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_role_list" + Translator.SUFFIX)
    @PostMapping(value = "/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.ROLE_READ)
    @I18n
    public Pager<List<RoleDTO>> paging(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RoleRequest request) {
        Map<String, Object> map = BeanUtils.objectToMap(request);
        map.put("ids", RoleUtils.getSystemRoleIds(request.getName()));
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, roleService.paging(map));
    }

    @GetMapping(value = "/delete/{roleId}")
    @RequiresPermissions(PermissionConstants.ROLE_DELETE)
    public void delete(@PathVariable String roleId) {
        roleService.delete(roleId);
    }

    @PostMapping("/add")
    @RequiresPermissions(PermissionConstants.ROLE_CREATE)
    public void insert(@RequestBody RoleOperate role) {
        roleService.insert(role);
    }

    @PostMapping("/update")
    @RequiresPermissions(PermissionConstants.ROLE_EDIT)
    public void update(@RequestBody RoleOperate role) {
        roleService.update(role);
    }

    @GetMapping
    @RequiresPermissions(value = {PermissionConstants.USER_READ, PermissionConstants.USER_ADD_ROLE}, logical = Logical.OR)
    @I18n
    public List<Role> roles() {
        String parentRoleId = SessionUtils.getUser().getParentRoleId();
        return roleService.roles(parentRoleId);
    }

    @RequiresPermissions(PermissionConstants.ROLE_CREATE)
    @GetMapping(value = "permissionTree/{roleId}")
    public List<TreeNode> getPermissionTree(@PathVariable String roleId) {
        return menuService.getPermissionByRoleId(roleId);
    }

    @RequiresPermissions(PermissionConstants.ROLE_EDIT)
    @GetMapping(value = "authorizePermission/{roleId}")
    public List<McTreeNode> authorizePermission(@PathVariable String roleId) {
        return roleService.authorizePermission(roleId);
    }
}
