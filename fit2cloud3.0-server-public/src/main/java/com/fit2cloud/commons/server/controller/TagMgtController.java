package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.Tag;
import com.fit2cloud.commons.server.base.domain.TagMapping;
import com.fit2cloud.commons.server.base.domain.TagMappingExample;
import com.fit2cloud.commons.server.base.domain.TagValue;
import com.fit2cloud.commons.server.base.mapper.TagMappingMapper;
import com.fit2cloud.commons.server.constants.PermissionConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.TagDTO;
import com.fit2cloud.commons.server.service.TagMappingService;
import com.fit2cloud.commons.server.service.TagService;
import com.fit2cloud.commons.server.tag.request.TagRequest;
import com.fit2cloud.commons.server.tag.request.TagValueRequest;
import com.fit2cloud.commons.server.utils.ValidatorUtil;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("tag")
@Api(tags = {Translator.PREFIX + "i18n_mc_tag_tag" + Translator.SUFFIX})
public class TagMgtController {
    @Resource
    private TagService tagService;
    @Resource
    private TagMappingService tagMappingService;
    @Resource
    private TagMappingMapper tagMappingMapper;

    @PostMapping("list/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.TAG_READ)
    @ApiOperation(Translator.PREFIX + "i18n_mc_tag_tag_list" + Translator.SUFFIX)
    public Pager<List<TagDTO>> selectTags(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody TagRequest request) {
        //为防止sql注入，对排序变量sort做正则校验
        if (StringUtils.isNotBlank(request.getSort()) && !ValidatorUtil.isSort(request.getSort())) {
            F2CException.throwException("field 'sort' does not match to the regular [A-Z, A-Z, 0-9,] specification!");
        }
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        List<TagDTO> tags = tagService.selectTags(BeanUtils.objectToMap(request));
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
    @PostMapping("delete/{tagId}")
    @RequiresPermissions(PermissionConstants.TAG_DELETE)
    public void deleteTag(@PathVariable String tagId) throws Exception {
        tagService.deleteTag(tagId);
    }


    @ApiOperation(Translator.PREFIX + "i18n_permission_tag_value_read_delete" + Translator.SUFFIX)
    @PostMapping("value/delete/{id}")
    @RequiresPermissions(PermissionConstants.TAG_VALUE_DELETE)
    public void deleteTagValue(@PathVariable String id) {
        tagService.deleteTagValue(id);
    }

    @PostMapping(value = "value/import")
    @RequiresPermissions(PermissionConstants.TAG_VALUE_IMPORT)
    public Integer importTagValue(@RequestParam("file") MultipartFile file, @RequestParam String tagKey, @RequestParam String tagId, @RequestParam(defaultValue = "false") boolean isClear) throws Exception {
        return tagService.importTagValue(file, tagKey, tagId, isClear);
    }


    @PostMapping("listAlls")
    public List<TagDTO> selectAllTags() {
        return tagService.selectAllTags();
    }

    @PostMapping("listAll")
    public List<TagDTO> selectAllTags(@RequestBody List<String> tagKeys) {
        return tagService.selectAllTags().stream().filter(tagDTO -> {
            for (String tagKey : tagKeys) {
                if (tagKey.equals(tagDTO.getTagKey())) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    @PostMapping("mapping/save")
    public void saveTagMapping(@RequestBody List<TagMapping> tagMappings) throws Exception {
        List<TagMapping> deleteTagMapping = null;
        List<TagMapping> saveTagMapping = null;
        if (CollectionUtils.isNotEmpty(tagMappings)) {
            deleteTagMapping = tagMappings.stream().filter(tagMapping -> tagMapping.getResourceType().equalsIgnoreCase("DELETE"))
                    .collect(Collectors.toList());
            saveTagMapping = tagMappings.stream().filter(tagMapping -> !tagMapping.getResourceType().equalsIgnoreCase("DELETE"))
                    .collect(Collectors.toList());
        }
        tagMappingService.saveTagMappings(saveTagMapping);
        tagMappingService.deleteTagMappings(deleteTagMapping);
    }

    @PostMapping(value = "mapping/batchSave")
    public void batchSaveTagMapping(@RequestBody List<TagMapping> tagMappings) throws Exception {
        List<TagMapping> saveTagMapping = null;
        if (org.apache.commons.collections.CollectionUtils.isEmpty(tagMappings)) {
            return;
        }
        List<String> serverIds = tagMappings.stream().map(TagMapping::getResourceId).distinct().collect(Collectors.toList());
        TagMappingExample tagMappingExample = new TagMappingExample();
        tagMappingExample.createCriteria().andResourceIdIn(serverIds).andTagIdEqualTo(tagMappings.get(0).getTagId());
        tagMappingMapper.deleteByExample(tagMappingExample);
        tagMappingService.saveTagMappings(tagMappings);
    }

    @PostMapping("getValues/{tagId}")
    public List<TagValue> getTagValues(@PathVariable String tagId) {
        Map params = new HashMap();
        params.put("tagId", tagId);
        return tagService.selectTagValues(params);
    }

    @PostMapping(value = "mapping/list")
    public List<TagMapping> listTagMapping(@RequestBody Map<String, String> params) {
        return tagMappingService.selectTagMappings(params);
    }

    @GetMapping(value = "list/tag")
    public List<TagDTO> getTagList() {
        return tagService.selectTags(new HashMap<>());
    }
}
