package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import com.fit2cloud.commons.server.base.mapper.TagMapper;
import com.fit2cloud.commons.server.base.mapper.TagMappingMapper;
import com.fit2cloud.commons.server.base.mapper.WorkspaceMapper;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.model.TagDTO;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.UUIDUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class TagMappingService {
    @Resource
    private TagMappingMapper tagMappingMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private OrganizationMapper organizationMapper;

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
            if(StringUtils.isEmpty(tagMapping.getTagId()) && StringUtils.isEmpty(tagMapping.getTagKey())){
                throw new Exception("The TagID and Tagkey cannot be empty at the same time.");
            }
            if(StringUtils.isEmpty(tagMapping.getTagKey()) && StringUtils.isNoneEmpty(tagMapping.getTagId())){
                tagMapping.setTagKey(tagMapper.selectByPrimaryKey(tagMapping.getTagId()).getTagKey());
            }
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
        List<Tag> tags = selectAllTags();
        List<String> collect = Optional.ofNullable(tags).orElse(new ArrayList<>()).stream().map(Tag::getTagId).collect(Collectors.toList());
        collect.add("-1");
        criteria.andTagIdIn(collect);

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

    public List<Tag> selectAllTags() {
        TagExample tagExample = new TagExample();
        TagExample.Criteria criteria = tagExample.createCriteria();
        criteria.andEnableEqualTo(Boolean.TRUE);
        SessionUser user = SessionUtils.getUser();
        if (user != null) {
            if (StringUtils.equalsIgnoreCase(RoleConstants.Id.ADMIN.name(), user.getParentRoleId())) {
                criteria.andScopeEqualTo(RoleConstants.Id.ADMIN.name());
            } else if (StringUtils.equalsIgnoreCase(RoleConstants.Id.ORGADMIN.name(), user.getParentRoleId())) {
                criteria.andScopeIn(Arrays.asList(RoleConstants.Id.ADMIN.name(), RoleConstants.Id.ORGADMIN.name()));
                List<String> orgTree = getOrgTree(user.getOrganizationId());
                criteria.andResourceIdIn(orgTree);
            } else if (StringUtils.equalsIgnoreCase(RoleConstants.Id.USER.name(), user.getParentRoleId())) {
                criteria.andScopeIn(Arrays.asList(RoleConstants.Id.ADMIN.name(), RoleConstants.Id.ORGADMIN.name(), RoleConstants.Id.USER.name()));
                List<String> orgTree = getOrgTree(user.getOrganizationId());
                orgTree.add(user.getWorkspaceId());
                criteria.andResourceIdIn(orgTree);
            }
        }
        tagExample.setOrderByClause("scope asc");
        List<Tag> tags = tagMapper.selectByExample(tagExample);
        return tags;
    }

    private List<String> getOrgTree(String orgId) {
        List<String> orgIds = new ArrayList<>();
        orgIds.add("");
        orgIds.add(orgId);
        while (StringUtils.isNotEmpty(orgId)) {
            Organization organization = organizationMapper.selectByPrimaryKey(orgId);
            if (StringUtils.isNotEmpty(organization.getPid())) {
                Organization organizationParent = organizationMapper.selectByPrimaryKey(organization.getPid());
                if (organizationParent != null) {
                    orgIds.add(organizationParent.getId());
                    orgId = organizationParent.getPid();
                }
            } else {
                orgId = null;
            }
        }
        return orgIds;
    }
}
