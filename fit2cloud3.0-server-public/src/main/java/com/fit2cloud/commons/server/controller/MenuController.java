package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.MenuPreference;
import com.fit2cloud.commons.server.base.domain.Module;
import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.model.ModuleDTO;
import com.fit2cloud.commons.server.model.TreeNode;
import com.fit2cloud.commons.server.service.MenuService;
import com.fit2cloud.commons.server.service.ModuleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Author: chunxing
 * Date: 2018/5/9  下午3:51
 * Description:
 */
@RestController
@RequestMapping(headers = "Accept=application/json")
public class MenuController {

    @Resource
    private MenuService menuService;
    @Resource
    private ModuleService moduleService;

    /**
     * 获取当前模块信息，菜单，权限
     *
     * @return
     */
    @GetMapping("module/menus")
    @I18n
    public ModuleDTO module() {
        return menuService.getModule();
    }

    /**
     * 获取所有菜单
     *
     * @return
     */
    @GetMapping("module/all")
    @I18n(I18nConstants.CLUSTER)//本地开发 能看见菜单
    public Map<String, Object> modules() {
        return menuService.getModules();
    }

    @GetMapping("module/permission/{roleId}")
    @I18n
    public TreeNode getModulePermissionByRoleId(@PathVariable String roleId) {
        return menuService.getModulePermissionByRoleId(roleId);
    }

    /**
     * 获取的系统模块 已授权 启用中
     *
     * @return
     */
    @GetMapping(value = "module/system/list")
    @I18n(I18nConstants.CLUSTER)
    public List<Module> getModuleList() {
        return moduleService.getSystemEnableModuleList();
    }


    @PostMapping("module/menu/preference")
    public void opMenuPreference(@RequestBody MenuPreference menuPreference) {
        menuService.opMenuPreference(menuPreference);
    }

    @PostMapping("module/menu/preference/sort")
    public void sortMenuPreference(@RequestBody List<MenuPreference> menuPreferences) {
        menuService.sortMenuPreference(menuPreferences);
    }

}
