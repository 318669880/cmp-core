package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.CloudAccountDTO;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.request.CloudAccountRequest;
import com.fit2cloud.mc.dto.request.CreateCloudAccountRequest;
import com.fit2cloud.mc.dto.request.UpdateCloudAccountRequest;
import com.fit2cloud.mc.service.AccountService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "cloud/account", headers = "Accept=application/json")
@Api(tags = Translator.PREFIX + "i18n_mc_swagger_cloud_account_tag" + Translator.SUFFIX)
public class AccountController {
    @Resource
    private AccountService accountService;

    @ApiOperation(Translator.PREFIX + "i18n_mc_swagger_cloud_account_list" + Translator.SUFFIX)
    @PostMapping("list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.CLOUD_ACCOUNT_READ)
    public Pager<List<CloudAccountDTO>> getCloudAccountList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudAccountRequest request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.getAccountList(BeanUtils.objectToMap(request)));
    }

    @ApiOperation(Translator.PREFIX + "i18n_mc_swagger_cloud_account_create" + Translator.SUFFIX)
    @PostMapping("add")
    @RequiresPermissions(PermissionConstants.CLOUD_ACCOUNT_CREATE)
    public CloudAccountDTO addAccount(@RequestBody CreateCloudAccountRequest request) {
        return accountService.addAccount(request);
    }

    @ApiOperation(Translator.PREFIX + "i18n_mc_swagger_cloud_account_edit" + Translator.SUFFIX)
    @PostMapping("update")
    @RequiresPermissions(PermissionConstants.CLOUD_ACCOUNT_EDIT)
    public CloudAccountDTO editAccount(@RequestBody UpdateCloudAccountRequest request) {
        return accountService.editAccount(request);
    }

    @ApiOperation(Translator.PREFIX + "i18n_permission_cloud_account_read_validate" + Translator.SUFFIX)
    @PostMapping("validate/{id}")
    @RequiresPermissions(PermissionConstants.CLOUD_ACCOUNT_VALIDATE)
    public Boolean validate(@PathVariable String id) {
        return accountService.validate(id);
    }

    @ApiOperation(Translator.PREFIX + "i18n_mc_swagger_cloud_account_delete_batch" + Translator.SUFFIX)
    @PostMapping(value = "batch/delete")
    @RequiresPermissions(PermissionConstants.CLOUD_ACCOUNT_BATCH_DELETE)
    public void deleteBatch(@RequestBody List<String> idList) {
        accountService.delete(idList);
    }

    @ApiOperation(Translator.PREFIX + "i18n_mc_swagger_cloud_account_sync_batch" + Translator.SUFFIX)
    @PostMapping(value = "batch/sync")
    @RequiresPermissions(PermissionConstants.CLOUD_ACCOUNT_BATCH_SYNC)
    public void sync(@RequestBody List<String> idList) {
        accountService.sync(idList);
    }


    @ApiOperation(Translator.PREFIX + "i18n_mc_swagger_cloud_account_delete" + Translator.SUFFIX)
    @PostMapping(value = "delete/{accountId}")
    @RequiresPermissions(PermissionConstants.CLOUD_ACCOUNT_DELETE)
    public void deleteAccount(@PathVariable String accountId) {
        accountService.delete(accountId);
    }

    @ApiOperation(Translator.PREFIX + "i18n_mc_swagger_cloud_account_sync" + Translator.SUFFIX)
    @PostMapping(value = "/sync/{accountId}")
    @RequiresPermissions(PermissionConstants.CLOUD_ACCOUNT_SYNC)
    public void syncAccount(@PathVariable String accountId) {
        accountService.syncResource(accountId);
    }

}
