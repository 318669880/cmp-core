package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.Module;
import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.service.ModuleService;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.service.SysStatsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("module")
public class ModuleController {

    @Resource
    private ModuleService moduleService;
    @Resource
    private SysStatsService sysStatsService;

    @GetMapping("list")
    @RequiresPermissions(PermissionConstants.MODULE_READ)
    @I18n(I18nConstants.CLUSTER)
    public List<Module> getAllModuleList() {
        return sysStatsService.getModuleList();
    }

    @RequestMapping("active/{moduleId}/{status}")
    @RequiresPermissions(PermissionConstants.MODULE_ACTIVE)
    public void activeModule(@PathVariable String moduleId, @PathVariable boolean status) {
        moduleService.activeModule(moduleId, status);
    }

    @PostMapping("add")
    @RequiresPermissions(PermissionConstants.MODULE_CREATE)
    public void addModule(@RequestBody Module module) {
        moduleService.addModule(module);
    }

    @PostMapping("update")
    @RequiresPermissions(PermissionConstants.MODULE_EDIT)
    public void updateModule(@RequestBody Module module) {
        moduleService.updateModule(module);
    }

    @PostMapping("delete/{id}")
    @RequiresPermissions(PermissionConstants.MODULE_DELETE)
    public void deleteModule(@PathVariable String id) {
        moduleService.deleteModule(id);
    }
}
