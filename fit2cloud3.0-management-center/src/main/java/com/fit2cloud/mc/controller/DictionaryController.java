package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.Dictionary;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.DictionaryOsDTO;
import com.fit2cloud.mc.service.DictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = Translator.PREFIX + "i18n_mc_swagger_tag_os" + Translator.SUFFIX)
@RequestMapping("/dictionary")
public class DictionaryController {
    @Resource
    private DictionaryService dictionaryService;

    @ApiOperation(Translator.PREFIX + "i18n_mc_swagger_os_list" + Translator.SUFFIX)
    @PostMapping("category/os/list")
    @RequiresPermissions(PermissionConstants.DICTIONARY_OS_READ)
    public List<DictionaryOsDTO> getOsCategoryList() {
        return dictionaryService.getOsCategoryList();
    }

    @ApiOperation(Translator.PREFIX + "i18n_permission_os_read_create" + Translator.SUFFIX)
    @PostMapping("category/os/add")
    @RequiresPermissions(PermissionConstants.DICTIONARY_OS_CREATE)
    public void addOsCategory(@RequestBody Dictionary dictionary) {
        dictionaryService.addOrEditOsCategory(dictionary, true);
    }

    @ApiOperation(Translator.PREFIX + "i18n_permission_os_read_edit" + Translator.SUFFIX)
    @PostMapping("category/os/update")
    @RequiresPermissions(PermissionConstants.DICTIONARY_OS_EDIT)
    public void editOsCategory(@RequestBody Dictionary dictionary) {
        dictionaryService.addOrEditOsCategory(dictionary, false);
    }

    @ApiOperation(Translator.PREFIX + "i18n_permission_os_read_delete" + Translator.SUFFIX)
    @PostMapping("category/os/delete/{id}")
    @RequiresPermissions(PermissionConstants.DICTIONARY_OS_DELETE)
    public void deleteOsCategory(@PathVariable String id) {
        dictionaryService.deleteOsCategory(id);
    }


    @ApiOperation(Translator.PREFIX + "i18n_permission_os_version_delete" + Translator.SUFFIX)
    @PostMapping("category/os/version/delete/{id}")
    @RequiresPermissions(PermissionConstants.DICTIONARY_OS_VERSION_DELETE)
    public void deleteOsVersion(@PathVariable String id) {
        dictionaryService.deleteOsVersion(id);
    }

    @ApiOperation(Translator.PREFIX + "i18n_permission_os_version_create" + Translator.SUFFIX)
    @PostMapping("category/os/{osId}/add/{version}")
    @RequiresPermissions(PermissionConstants.DICTIONARY_OS_VERSION_CREATE)
    public void addOsVersion(@PathVariable String osId, @PathVariable String version) {
        dictionaryService.addOsVersion(osId, version);
    }
}
