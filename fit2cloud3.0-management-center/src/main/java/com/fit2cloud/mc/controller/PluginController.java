package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.PluginWithBLOBs;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.PluginInfoDTO;
import com.fit2cloud.mc.dto.request.PluginRequest;
import com.fit2cloud.mc.service.PluginService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "plugin")
@Api(tags = Translator.PREFIX + "i18n_mc_plugin_tag" + Translator.SUFFIX)
public class PluginController {
    @Resource
    private PluginService pluginService;

    @RequestMapping(value = "all")
    @RequiresPermissions(PermissionConstants.CLOUD_ACCOUNT_READ)
    public List<PluginWithBLOBs> getAllPlugin() {
        return pluginService.getAllPlugin();
    }

    @RequestMapping(value = "{pluginName}", method = RequestMethod.GET)
    @RequiresPermissions(PermissionConstants.CLOUD_ACCOUNT_CREATE)
    @I18n
    public Object getCredential(@PathVariable String pluginName) {
        return pluginService.getCredential(pluginName);
    }

    @ApiOperation(Translator.PREFIX + "i18n_mc_plugin_list" + Translator.SUFFIX)
    @PostMapping("list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.PLUGIN_READ)
    public Pager<List<PluginInfoDTO>> getPluginList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody PluginRequest request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, pluginService.getPluginList(BeanUtils.objectToMap(request)));
    }
}
