package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.constants.PermissionConstants;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.model.LinkValueDTO;
import com.fit2cloud.commons.server.model.TreeNode;
import com.fit2cloud.commons.server.process.*;
import com.fit2cloud.commons.server.process.dto.EventExecutor;
import com.fit2cloud.commons.server.process.dto.UserDTO;
import com.fit2cloud.commons.server.service.RoleCommonService;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/flow/design")
public class ProcessDesignController {

    @Resource
    private ProcessModelService processModelService;

    @Resource
    private ProcessEventService processEventService;

    @Resource
    private ProcessMessageService processMessageService;

    @Resource
    private ProcessRoleService processRoleService;

    @Resource
    private ProcessLinkService processLinkService;

    @Resource
    private RoleCommonService roleCommonService;

    @GetMapping(value = "/model/list/{goPage}/{pageSize}")
    @I18n
    public Pager<List<FlowModel>> listModel(@PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, processModelService.listModel());
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/model/list")
    @I18n
    public List<FlowModel> listModel() {
        return processModelService.listModel();
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/model/get/{modelId}")
    @I18n
    public FlowModel getModelById(@PathVariable String modelId) {
        return processModelService.getModelById(modelId);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/model/add")
    public int addModel(@RequestBody FlowModel model) {
        return processModelService.addModel(model);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/model/update")
    public int updateModel(@RequestBody FlowModel model) {
        return processModelService.updateModel(model);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/model/copy/{oldId}/{newId}")
    public int copyModel(@PathVariable String oldId, @PathVariable String newId) {
        return processModelService.copyModel(oldId, newId);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/model/delete/{modelId}")
    public int deleteModel(@PathVariable String modelId) {
        return processModelService.deleteModel(modelId);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/model/publish/{modelId}")
    public int publishModel(@PathVariable String modelId) {
        return processModelService.publishModel(modelId);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/deploy/history/{modelId}")
    @I18n
    public List<FlowDeploy> getDeployHistory(@PathVariable String modelId) {
        return processModelService.listDeployHistory(modelId);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/deploy/last/{modelId}")
    @I18n
    public FlowDeploy getLastVersionDeploy(@PathVariable String modelId) {
        return processModelService.getLastVersionDeploy(modelId);
    }

    // Event
    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/event/list/{modelId}/{step}/{activityId}")
    @I18n
    public List<FlowEvent> listProcessEvent(@PathVariable String modelId, @PathVariable int step, @PathVariable String activityId) {
        return processEventService.listProcessEvent(modelId, step, activityId);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/event/add")
    public int addEvent(@RequestBody FlowEvent event) {
        return processEventService.addEvent(event);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/event/update")
    public int updateEvent(@RequestBody FlowEvent event) {
        return processEventService.updateEvent(event);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/event/delete/{id}")
    public int deleteEvent(@PathVariable int id) {
        return processEventService.deleteEvent(id);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/event/executor/list")
    public List<EventExecutor> listProcessEventClass() {
        return processEventService.listProcessEventClass();
    }

    // Message
    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/notification/list/{modelId}/{step}/{activityId}")
    @I18n
    public List<FlowNotificationConfig> listConfig(@PathVariable String modelId, @PathVariable int step, @PathVariable String activityId) {
        return processMessageService.listConfig(modelId, step, activityId);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/notification/add")
    public int addConfig(@RequestBody FlowNotificationConfig config) {
        return processMessageService.addConfig(config);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/notification/update")
    public int updateConfig(@RequestBody FlowNotificationConfig config) {
        return processMessageService.updateConfig(config);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/notification/delete/{id}")
    public int deleteConfig(@PathVariable int id) {
        return processMessageService.deleteConfig(id);
    }

    @PostMapping(value = "/user/list")
    public List<UserDTO> listUser(@RequestBody Map<String, String> map) {
        return processModelService.listUser(map);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/user/list/{goPage}/{pageSize}")
    public Pager<List<UserDTO>> listUser(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, String> map) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, processModelService.listUser(map));
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/user/list/{roleKey}")
    public List<UserDTO> listRoleUsers(@PathVariable String roleKey) {
        return processModelService.listRoleUsers(roleKey);
    }

    // Process Role
    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/role/list/{goPage}/{pageSize}")
    @I18n
    public Pager<List<FlowRole>> listProcessRoles(@PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, processRoleService.listProcessRoles());
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/role/list/all")
    @I18n
    public List<FlowRole> listAllProcessRoles() {
        return processRoleService.listProcessRoles();
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/role/add")
    public int addRole(@RequestBody FlowRole role) {
        return processRoleService.addRole(role);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/role/update")
    public int updateRole(@RequestBody FlowRole role) {
        return processRoleService.updateRole(role);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/role/delete")
    public int deleteRole(@RequestParam String key) {
        return processRoleService.deleteRole(key);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/role/user/save")
    public int saveRoleUsers(@RequestBody List<String> ids, @RequestParam String key) {
        return processRoleService.saveRoleUsers(ids, key);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/role/used")
    @I18n
    public List<Map<String, Object>> roleUsed(@RequestBody List<String> roleKeys) {
        return processModelService.getRoleUsed(roleKeys);
    }

    // System Role
    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/role/system/list/all")
    @I18n
    public List<Role> listAllSystemRoles() {
        return roleCommonService.listSystemRoles();
    }

    // 修复历史数据 2020-1-21以后可以删除这个功能
    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/fixed/{modelId}")
    public void fixedHistoryData(@PathVariable String modelId) {
        processModelService.fixedHistoryData(modelId);
    }

    // Process Link
    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/link/list/{goPage}/{pageSize}")
    public Pager<List<FlowLink>> listProcessLinks(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, String> request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, processLinkService.listProcessLinks(request));
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/link/list/all")
    public List<FlowLink> listAll() {
        Map<String, String> request = new HashMap<>();
        request.put("sql", "flow_link.link_key desc");
        return processLinkService.listProcessLinks(request);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/link/models")
    public List<Map<String, Object>> linkModels(@RequestBody List<String> linkKeys) {
        return processModelService.getModelsByFlowLink(linkKeys);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/link/delete")
    public int deleteLink(@RequestParam String key) {
        return processLinkService.deleteLink(key);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/link/add")
    public int addLink(@RequestBody FlowLink link) {
        return processLinkService.addLink(link);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/link/update")
    public int updateLink(@RequestBody FlowLink link) {
        return processLinkService.updateLink(link);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/link/value/list/{linkKey}/{goPage}/{pageSize}")
    @I18n
    public Pager<List<FlowLinkValue>> listProcessLinks(@PathVariable String linkKey, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, processLinkService.listProcessLinkValues(linkKey));
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @GetMapping(value = "/link/value/delete/{linkValueId}")
    public int deleteLinkValue(@PathVariable String linkValueId) {
        return processLinkService.deleteLinkValue(linkValueId);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/link/value/add")
    public void addLinkValue(@RequestBody LinkValueDTO linkValue) {
        processLinkService.addLinkValue(linkValue);
    }

    @RequiresPermissions(PermissionConstants.FLOW_MANAGER)
    @PostMapping(value = "/link/value/update")
    public int updateLinkValue(@RequestBody  FlowLinkValue linkValue) {
        return processLinkService.updateLinkValue(linkValue);
    }

    @GetMapping(value = "/workspace/tree/{id}")
    public List<TreeNode> getLinkValueWorkspaceTree(@PathVariable String id) {
        return processLinkService.getLinkValueWorkspaceTree(id);
    }

    @PostMapping(value = "/workspace/tree/update/{id}")
    public void updateLinkValueWorkspaceTree(@RequestBody LinkValueDTO linkValueDTO, @PathVariable String id) {
        processLinkService.updateLinkValueWorkspaceTree(id, linkValueDTO);
    }
}
