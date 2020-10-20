package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.TagMapping;
import com.fit2cloud.commons.server.base.domain.TagMappingExample;
import com.fit2cloud.commons.server.base.mapper.TagMapper;
import com.fit2cloud.commons.server.base.mapper.TagMappingMapper;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.TagDTO;
import com.fit2cloud.commons.utils.UUIDUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class TagMappingService {
    @Resource
    private TagMappingMapper tagMappingMapper;
    @Resource
    private TagService tagService;
    @Resource
    private TagMapper tagMapper;

    public void saveTagMapping(TagMapping tagMapping) throws Exception {
        valid(tagMapping, true);
        tagMapping.setId(UUIDUtil.newUUID());
        if (tagMapping.getCreateTime() == null) {
            tagMapping.setCreateTime(System.currentTimeMillis());
        }
        tagMappingMapper.insert(tagMapping);
    }

    public void saveTagMappings(List<TagMapping> tagMappings) throws Exception {
        if (CollectionUtils.isEmpty(tagMappings)) {
            return;
        }
        for (TagMapping tagMapping : tagMappings) {
            tagMapping.setCreateTime(System.currentTimeMillis());
            tagMapping.setTagKey(tagMapper.selectByPrimaryKey(tagMapping.getTagId()).getTagKey());
            saveTagMapping(tagMapping);
        }
    }

    public List<TagMapping> selectTagMappings(Map<String, String> params) {
        TagMappingExample tagMappingExample = new TagMappingExample();
        TagMappingExample.Criteria criteria = tagMappingExample.createCriteria();

        if (params != null) {
            if (StringUtils.isNotEmpty(params.get("resourceId"))) {
                criteria.andResourceIdEqualTo(params.get("resourceId"));
            }
            if (StringUtils.isNotEmpty(params.get("resourceType"))) {
                criteria.andResourceTypeEqualTo(params.get("resourceType"));
            }
            if (StringUtils.isNotEmpty(params.get("tagKey"))) {
                criteria.andTagKeyEqualTo(params.get("tagKey"));
            }
            if (StringUtils.isNotEmpty(params.get("tagValueId"))) {
                criteria.andTagValueIdEqualTo(params.get("tagValueId"));
            }
        }
        tagMappingExample.setOrderByClause("create_time");

        //只返回enable的标签对应的映射关系
        List<TagDTO> tags = tagService.selectAllTags();
        List<String> collect = Optional.ofNullable(tags).orElse(new ArrayList<>()).stream().map(TagDTO::getTagKey).collect(Collectors.toList());
        collect.add("-1");
        criteria.andTagKeyIn(collect);

        return tagMappingMapper.selectByExample(tagMappingExample);
    }

    public void deleteTagMappings(List<TagMapping> tagMappings) throws Exception {
        if (CollectionUtils.isNotEmpty(tagMappings)) {
            for (TagMapping tagMapping : tagMappings) {
                valid(tagMapping, false);
                TagMappingExample tagMappingExample = new TagMappingExample();
                tagMappingExample.createCriteria().andResourceIdEqualTo(tagMapping.getResourceId())
                        .andTagKeyEqualTo(tagMapping.getTagKey())
                        .andTagValueIdEqualTo(tagMapping.getTagValueId());

                tagMappingMapper.deleteByExample(tagMappingExample);
            }
        }
    }

    private void valid(TagMapping tagMapping, boolean validDB) {
        if (tagMapping == null) {
            F2CException.throwException(Translator.get("i18n_ex_tag_relationship_empty"));
        }
        if (StringUtils.isBlank(tagMapping.getTagKey())) {
            F2CException.throwException(Translator.get("i18n_ex_tag_key_empty"));
        }
        if (StringUtils.isBlank(tagMapping.getTagValueId())) {
            F2CException.throwException(Translator.get("i18n_ex_tag_value_empty"));
        }
        if (StringUtils.isBlank(tagMapping.getResourceId())) {
            F2CException.throwException(Translator.get("i18n_ex_resource_id_empty"));
        }
        if (StringUtils.isBlank(tagMapping.getResourceType())) {
            F2CException.throwException(Translator.get("i18n_ex_resource_type_empty"));
        }
        if (validDB) {
            TagMappingExample tagMappingExample = new TagMappingExample();
            tagMappingExample.createCriteria().andResourceIdEqualTo(tagMapping.getResourceId())
                    .andTagKeyEqualTo(tagMapping.getTagKey())
                    .andTagValueIdEqualTo(tagMapping.getTagValueId());
            if (CollectionUtils.isNotEmpty(tagMappingMapper.selectByExample(tagMappingExample))) {
                F2CException.throwException(Translator.get("i18n_ex_relationship_exist"));
            }
        }
    }
}
