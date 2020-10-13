package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.Tag;
import com.fit2cloud.commons.server.base.domain.TagValue;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.service.TagService;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.request.TagRequest;
import com.fit2cloud.mc.dto.request.TagValueRequest;
import com.fit2cloud.mc.utils.ValidatorUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("tag")
@Api(tags = {Translator.PREFIX + "i18n_mc_tag_tag" + Translator.SUFFIX})
public class TagController {
    @Resource
    private TagService tagService;

    @PostMapping("list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.TAG_READ)
    @ApiOperation(Translator.PREFIX + "i18n_mc_tag_tag_list" + Translator.SUFFIX)
    public Pager<List<Tag>> selectTags(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody TagRequest request) {
        //为防止sql注入，对排序变量sort做正则校验
        if (StringUtils.isNotBlank(request.getSort()) && ValidatorUtil.isSort(request.getSort()) == false) {
            F2CException.throwException("field 'sort' does not match to the regular [A-Z, A-Z, 0-9,] specification!");
        }
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        List<Tag> tags = tagService.selectTags(BeanUtils.objectToMap(request));
        return PageUtils.setPageInfo(page, tags);
    }

    @PostMapping("value/list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.TAG_VALUE_READ)
    @ApiOperation(Translator.PREFIX + "i18n_mc_tag_tag_value_list" + Translator.SUFFIX)
    public Pager<List<TagValue>> selectTagValues(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody TagValueRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        List<TagValue> tags = tagService.selectTagValues(BeanUtils.objectToMap(request));
        return PageUtils.setPageInfo(page, tags);
    }


    @ApiOperation(Translator.PREFIX + "i18n_permission_tag_read_create" + Translator.SUFFIX)
    @PostMapping("add")
    @RequiresPermissions(PermissionConstants.TAG_CREATE)
    public void createTag(@RequestBody Tag tag) {
        tagService.saveTag(tag);
    }

    @ApiOperation(Translator.PREFIX + "i18n_permission_tag_read_edit" + Translator.SUFFIX)
    @PostMapping("update")
    @RequiresPermissions(PermissionConstants.TAG_EDIT)
    public void updateTag(@RequestBody Tag tag) {
        tagService.saveTag(tag);
    }

    @ApiOperation(Translator.PREFIX + "i18n_permission_tag_value_read_create" + Translator.SUFFIX)
    @PostMapping("value/add")
    @RequiresPermissions(PermissionConstants.TAG_VALUE_EDIT)
    public void createTagValue(@RequestBody TagValue tagValue) {
        tagService.saveTagValue(tagValue);
    }

    @ApiOperation(Translator.PREFIX + "i18n_permission_tag_value_read_edit" + Translator.SUFFIX)
    @PostMapping("value/update")
    @RequiresPermissions(PermissionConstants.TAG_VALUE_CREATE)
    public void updateTagValue(@RequestBody TagValue tagValue) {
        tagService.saveTagValue(tagValue);
    }

    @ApiOperation(Translator.PREFIX + "i18n_permission_tag_read_delete" + Translator.SUFFIX)
    @PostMapping("delete/{tagKey}")
    @RequiresPermissions(PermissionConstants.TAG_DELETE)
    public void deleteTag(@PathVariable String tagKey) throws Exception {
        tagService.deleteTag(tagKey);
    }


    @ApiOperation(Translator.PREFIX + "i18n_permission_tag_value_read_delete" + Translator.SUFFIX)
    @PostMapping("value/delete/{id}")
    @RequiresPermissions(PermissionConstants.TAG_VALUE_DELETE)
    public void deleteTagValue(@PathVariable String id) {
        tagService.deleteTagValue(id);
    }

    @PostMapping(value = "value/import")
    @RequiresPermissions(PermissionConstants.TAG_VALUE_IMPORT)
    public void importTagValue(@RequestParam("file") MultipartFile file, @RequestParam String tagKey, @RequestParam(defaultValue = "false") boolean isClear) throws Exception {
        tagService.importTagValue(file, tagKey, isClear);
    }
}
