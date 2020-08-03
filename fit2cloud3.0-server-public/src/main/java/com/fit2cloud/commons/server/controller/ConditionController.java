package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.model.CloudAccountDTO;
import com.fit2cloud.commons.server.model.UserTooltip;
import com.fit2cloud.commons.server.model.WorkspaceOrganization;
import com.fit2cloud.commons.server.service.ConditionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("condition")
public class ConditionController {

    @Resource
    private ConditionService conditionService;

    @GetMapping("workspace")
    @I18n
    public List<Workspace> getWorkspaceList(@RequestParam(required = false) String organizationId) {
        return conditionService.getWorkspaceList(organizationId);
    }

    @GetMapping("cloud/account/{type}")
    public List<CloudAccountDTO> getCloudAccountList(@PathVariable String type) {
        return conditionService.getCloudAccountList(type);
    }

    @GetMapping("organization")
    public List<Organization> getOrganizationList() {
        return conditionService.getOrganizationList();
    }

    /**
     * 获取云账号ID
     *
     * @return
     */
    @GetMapping("{id}/cloud/account")
    public Map<String, String> getCloudAccountNameById(@PathVariable String id) {
        return conditionService.getCloudAccountNameById(id);
    }

    @GetMapping("{id}/organization")
    public String getOrganizationNameById(@PathVariable String id) {
        return conditionService.getOrganizationNameById(id);
    }

    @GetMapping("{id}/workspace")
    @I18n
    public WorkspaceOrganization getWorkspaceOrganizationByWorkspaceId(@PathVariable String id) {
        return conditionService.getWorkspaceOrganizationByWorkspaceId(id);
    }

    @GetMapping("user/{keyWord}")
    public List<UserTooltip> searchUserByKeyWord(@PathVariable String keyWord) {
        return conditionService.searchUser(keyWord);
    }

    @PostMapping("user/tooltip")
    public List<UserTooltip> getUserTooltipByIds(@RequestBody List<String> ids) {
        return conditionService.getUserTooltipByIds(ids);
    }
}
