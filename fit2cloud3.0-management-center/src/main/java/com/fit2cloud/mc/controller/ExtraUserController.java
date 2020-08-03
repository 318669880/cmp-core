package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.ExtraUser;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.RoleInfo;
import com.fit2cloud.mc.dto.request.ExtraUserRequest;
import com.fit2cloud.mc.service.ExtraUserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: chunxing
 * Date: 2018/6/27  下午6:54
 * Description:
 */
@Api(tags = Translator.PREFIX + "i18n_mc_swagger_extra_tag" + Translator.SUFFIX)
@RequestMapping("extra/user")
@RestController
public class ExtraUserController {

    @Resource
    private ExtraUserService extraUserService;

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_swagger_extra_list" + Translator.SUFFIX)
    @PostMapping(value = "/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.KEYCLOAK_SETTING_SYNC)
    public Pager<List<ExtraUser>> paging(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ExtraUserRequest request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, extraUserService.paging(BeanUtils.objectToMap(request)));
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_swagger_extra_sync" + Translator.SUFFIX)
    @GetMapping("/sync")
    @RequiresPermissions(value = PermissionConstants.KEYCLOAK_SETTING_SYNC)
    public void sync() {
        extraUserService.reloadSync();
    }

    @RequiresPermissions(PermissionConstants.USER_IMPORT)
    @PostMapping("/import")
    @Transactional(rollbackFor = Exception.class)
    public void importUser(@RequestBody List<RoleInfo> roleInfoList, @RequestHeader List<String> ids) throws Exception {
        extraUserService.importUser(roleInfoList, ids);
    }
}
