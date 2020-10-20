package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.TagMapper;
import com.fit2cloud.commons.server.base.mapper.TagMappingMapper;
import com.fit2cloud.commons.server.base.mapper.TagValueMapper;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.model.TagDTO;
import com.fit2cloud.commons.server.utils.CharsetUtils;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class TagService {
    @Resource
    private TagMapper tagMapper;
    @Resource
    private TagValueMapper tagValueMapper;
    @Resource
    private TagMappingMapper tagMappingMapper;
    @Resource
    private CharsetUtils charsetUtils;

    public List<Tag> selectTags(Map<String, Object> params) {
        TagExample example = new TagExample();
        TagExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank((String) params.get("tagKey"))) {
            criteria.andTagKeyLike((String) params.get("tagKey"));
        }
        if (StringUtils.isNotBlank((String) params.get("tagAlias"))) {
            criteria.andTagAliasLike((String) params.get("tagAlias"));
        }
        if (StringUtils.isNotBlank((String) params.get("sort"))) {
            example.setOrderByClause((String) params.get("sort"));
        }
        SessionUser user = SessionUtils.getUser();
        if (user != null) {
            if (StringUtils.equalsIgnoreCase(RoleConstants.Id.ADMIN.name(), user.getParentRoleId())) {
                criteria.andScopeEqualTo(RoleConstants.Id.ADMIN.name());
            } else if (StringUtils.equalsIgnoreCase(RoleConstants.Id.ORGADMIN.name(), user.getParentRoleId())) {
                criteria.andScopeIn(Arrays.asList(RoleConstants.Id.ADMIN.name(), RoleConstants.Id.ORGADMIN.name()));
                criteria.andResourceIdIn(Arrays.asList("", user.getOrganizationId()));
            } else if (StringUtils.equalsIgnoreCase(RoleConstants.Id.USER.name(), user.getParentRoleId())) {
                criteria.andScopeIn(Arrays.asList(RoleConstants.Id.ADMIN.name(), RoleConstants.Id.ORGADMIN.name(), RoleConstants.Id.USER.name()));
                criteria.andResourceIdIn(Arrays.asList("", user.getOrganizationId(), user.getWorkspaceId()));
            }
        }
        return tagMapper.selectByExample(example);
    }

    public List<TagDTO> selectAllTags() {
        TagExample tagExample = new TagExample();
        TagExample.Criteria criteria = tagExample.createCriteria();
        criteria.andEnableEqualTo(Boolean.TRUE);
        SessionUser user = SessionUtils.getUser();
        if (user != null) {
            if (StringUtils.equalsIgnoreCase(RoleConstants.Id.ADMIN.name(), user.getParentRoleId())) {
                criteria.andScopeEqualTo(RoleConstants.Id.ADMIN.name());
            } else if (StringUtils.equalsIgnoreCase(RoleConstants.Id.ORGADMIN.name(), user.getParentRoleId())) {
                criteria.andScopeIn(Arrays.asList(RoleConstants.Id.ADMIN.name(), RoleConstants.Id.ORGADMIN.name()));
                criteria.andResourceIdIn(Arrays.asList("", user.getOrganizationId()));
            } else if (StringUtils.equalsIgnoreCase(RoleConstants.Id.USER.name(), user.getParentRoleId())) {
                criteria.andScopeIn(Arrays.asList(RoleConstants.Id.ADMIN.name(), RoleConstants.Id.ORGADMIN.name(), RoleConstants.Id.USER.name()));
                criteria.andResourceIdIn(Arrays.asList("", user.getOrganizationId(), user.getWorkspaceId()));
            }
        }
        List<Tag> tags = tagMapper.selectByExample(tagExample);
        List<TagValue> tagValues = tagValueMapper.selectByExample(null);

        if (CollectionUtils.isEmpty(tags) || CollectionUtils.isEmpty(tagValues)) {
            return new ArrayList<>();
        }

        Map<String, List<TagValue>> valueMap = new HashMap<>();

        tagValues.forEach(tagValue -> {
            valueMap.putIfAbsent(tagValue.getTagId(), new ArrayList<>());
            valueMap.get(tagValue.getTagId()).add(tagValue);
        });

        List<TagDTO> result = new ArrayList<>();

        tags.forEach(tag -> {
            if (CollectionUtils.isNotEmpty(valueMap.get(tag.getTagId()))) {
                TagDTO tagDTO = new TagDTO();
                BeanUtils.copyBean(tagDTO, tag);
                tagDTO.setTagValues(valueMap.get(tag.getTagId()));
                result.add(tagDTO);
            }
        });

        return result;
    }

    public Object saveTag(Tag tag) {
        if (tag == null) {
            F2CException.throwException(Translator.get("i18n_ex_tag_empty"));
        }
        if (StringUtils.isBlank(tag.getTagKey())) {
            F2CException.throwException(Translator.get("i18n_ex_tag_key_empty"));
        }
        if (StringUtils.isBlank(tag.getTagAlias())) {
            F2CException.throwException(Translator.get("i18n_ex_tag_alias_empty"));
        }
        if (StringUtils.isBlank(tag.getTagId())) {
            tag.setTagId(UUIDUtil.newUUID());
            tag.setCreateTime(System.currentTimeMillis());
            TagExample tagsExample = new TagExample();
            TagExample.Criteria criteria = tagsExample.createCriteria();
            criteria.andTagKeyEqualTo(tag.getTagKey());

            SessionUser user = SessionUtils.getUser();
            if (user != null) {
                if (StringUtils.equalsIgnoreCase(RoleConstants.Id.ADMIN.name(), user.getParentRoleId())) {
                    criteria.andScopeEqualTo(RoleConstants.Id.ADMIN.name());
                    tag.setScope(RoleConstants.Id.ADMIN.name());
                    tag.setResourceId("");
                } else if (StringUtils.equalsIgnoreCase(RoleConstants.Id.ORGADMIN.name(), user.getParentRoleId())) {
                    criteria.andScopeEqualTo(RoleConstants.Id.ORGADMIN.name());
                    criteria.andResourceIdEqualTo(user.getOrganizationId());
                    tag.setScope(RoleConstants.Id.ORGADMIN.name());
                    tag.setResourceId(user.getOrganizationId());
                } else if (StringUtils.equalsIgnoreCase(RoleConstants.Id.USER.name(), user.getParentRoleId())) {
                    criteria.andScopeEqualTo(RoleConstants.Id.USER.name());
                    criteria.andResourceIdEqualTo(user.getWorkspaceId());
                    tag.setScope(RoleConstants.Id.USER.name());
                    tag.setResourceId(user.getWorkspaceId());
                }
            } else {
                tag.setScope(RoleConstants.Id.ADMIN.name());
                tag.setResourceId("");
            }


            if (tagMapper.countByExample(tagsExample) > 0) {
                F2CException.throwException(Translator.get("i18n_ex_tag_exist"));
            }
            return tagMapper.insertSelective(tag);
        } else {
            return tagMapper.updateByPrimaryKeySelective(tag);
        }
    }

    public Object deleteTag(String tagId) throws Exception {
        if (StringUtils.isBlank(tagId)) {
            F2CException.throwException("tag id can not be null.");
        }
        TagValueExample tagValueExample = new TagValueExample();
        tagValueExample.createCriteria().andTagIdEqualTo(tagId);
        List<TagValue> tagValues = tagValueMapper.selectByExample(tagValueExample);
        if (CollectionUtils.isNotEmpty(tagValues)) {
            List<String> tagValueIds = new ArrayList<>();
            tagValues.forEach(ele -> tagValueIds.add(ele.getId()));
            TagMappingExample tagMappingExample = new TagMappingExample();
            tagMappingExample.createCriteria().andTagValueIdIn(tagValueIds);
            tagMappingMapper.deleteByExample(tagMappingExample);
        }
        tagValueMapper.deleteByExample(tagValueExample);
        TagExample tagExample = new TagExample();
        tagExample.createCriteria().andTagIdEqualTo(tagId);
        return tagMapper.deleteByExample(tagExample);
    }

    public List<TagValue> selectTagValues(Map<String, Object> params) {
        TagValueExample example = new TagValueExample();
        TagValueExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank((String) params.get("tagId"))) {
            criteria.andTagIdEqualTo((String) params.get("tagId"));
        }
        if (StringUtils.isNotBlank((String) params.get("tagKey"))) {
            criteria.andTagKeyEqualTo((String) params.get("tagKey"));
        }
        if (StringUtils.isNotBlank((String) params.get("tagValueAlias"))) {
            criteria.andTagValueAliasLike((String) params.get("tagValueAlias"));
        }
        if (StringUtils.isNotBlank((String) params.get("tagValue"))) {
            criteria.andTagValueLike((String) params.get("tagValue"));
        }
        if (StringUtils.isNotBlank((String) params.get("sort"))) {
            example.setOrderByClause((String) params.get("sort"));
        }
        return tagValueMapper.selectByExample(example);
    }

    public Object saveTagValue(TagValue tagValue) {
        if (tagValue == null) {
            F2CException.throwException(Translator.get("i18n_ex_tag_empty"));
        }
        if (StringUtils.isBlank(tagValue.getTagKey())) {
            F2CException.throwException(Translator.get("i18n_ex_tag_key_empty"));
        }
        if (StringUtils.isBlank(tagValue.getTagValue())) {
            F2CException.throwException(Translator.get("i18n_ex_tag_value_empty"));
        }
        if (StringUtils.isBlank(tagValue.getTagValueAlias())) {
            F2CException.throwException(Translator.get("i18n_ex_tag_alias_empty"));
        }
        if (StringUtils.isBlank(tagValue.getId())) {
            tagValue.setId(UUIDUtil.newUUID());
            tagValue.setCreateTime(System.currentTimeMillis());
            TagValueExample tagValueExample = new TagValueExample();
            tagValueExample.createCriteria().andTagKeyEqualTo(tagValue.getTagKey())
                    .andTagValueEqualTo(tagValue.getTagValue()).andTagIdEqualTo(tagValue.getTagId());
            if (tagValueMapper.countByExample(tagValueExample) > 0) {
                F2CException.throwException(Translator.get("i18n_ex_tag_value_exist"));
            }
            return tagValueMapper.insertSelective(tagValue);
        } else {
            return tagValueMapper.updateByPrimaryKeySelective(tagValue);
        }

    }

    public Object deleteTagValue(String id) {
        if (StringUtils.isBlank(id)) {
            F2CException.throwException("tagValue.id cannot be null");
        }
        TagMappingExample tagMappingExample = new TagMappingExample();
        tagMappingExample.createCriteria().andTagValueIdEqualTo(id);
        tagMappingMapper.deleteByExample(tagMappingExample);
        return tagValueMapper.deleteByPrimaryKey(id);
    }

    /*private boolean containChinese(String str) throws Exception{
        int len = str.length();
        for(int i = 0;i < len;i ++) {
            String temp = temp = URLEncoder.encode(str.charAt(i) + "", "utf-8");
            if(temp.equals(str.charAt(i) + ""))
                continue;
            String[] codes = temp.split("%");
            //判断是中文还是字符(下面判断不精确，部分字符没有包括)
            for(String code:codes)
            {
                if(code.compareTo("40") > 0)
                    return true;
            }
        }
        return false;
    }*/

    public int importTagValue(MultipartFile files, String tagKey, String tagId, boolean isClear) throws Exception {
        if (files == null) {
            F2CException.throwException("files cannot be null");
        }
        if (files.isEmpty()) {
            F2CException.throwException(Translator.get("i18n_ex_tag_import_empty"));
        }
        if (StringUtils.isBlank(tagKey)) {
            F2CException.throwException(Translator.get("i18n_ex_tag_key_empty"));
        }

        int count = 0;
        // 清空导入
        if (isClear) {
            TagValueExample example = new TagValueExample();
            example.createCriteria().andTagKeyEqualTo(tagKey).andTagIdEqualTo(tagId);
            List<TagValue> tagValues = tagValueMapper.selectByExample(example);

            TagMappingExample tagMappingExample = new TagMappingExample();
            TagMappingExample.Criteria criteria = tagMappingExample.createCriteria();
            criteria.andTagKeyEqualTo(tagKey);

            if (CollectionUtils.isNotEmpty(tagValues)) {
                List<String> tagValueIds = new ArrayList<>();
                tagValues.forEach((ele) -> tagValueIds.add(ele.getId()));
                criteria.andTagValueIdIn(tagValueIds);
            }

            tagMappingMapper.deleteByExample(tagMappingExample);
            tagValueMapper.deleteByExample(example);
        }

        InputStream inputStream = files.getInputStream();
        String charSetName = charsetUtils.getCharsetName(inputStream);
        LogUtil.info("The charSetName is [" + charSetName + "]");
        //try (BufferedReader reader = new BufferedReader(new InputStreamReader(files.getInputStream()))) {
        /**
         * 这里构造InputStreamReader时加上编码方式
         */
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charSetName))) {
            String line = reader.readLine();
            // 检查首行
            if (line != null) {
                String[] data = line.split(",");
                if (data.length != 3) {
                    F2CException.throwException(Translator.get("i18n_ex_tag_import_column_count"));
                }
            }
            line = reader.readLine();
            while (line != null) {
                /*if(containChinese(line) && "ISO-8859-1".equals(charSetName)){*/
                // 由于icu4j经常把GB2312读成ISO-8859-1且 使用gb2312编码读取iso编码 不会乱码
                // 所以ISO-8859-1完全转为gb2312处理
                if ("ISO-8859-1".equalsIgnoreCase(charSetName)) {
                    line = new String(line.getBytes(charSetName), "GB2312");
                }
                String[] data = line.split(",");

                if (!StringUtils.equals(tagKey, data[0])) {
                    F2CException.throwException(Translator.get("i18n_ex_tag_import_not_match"));
                }
                if (StringUtils.isBlank(data[1])) {
                    F2CException.throwException(Translator.get("i18n_ex_tag_import_value_empty"));
                }

                TagValue tagValue = new TagValue();
                tagValue.setCreateTime(System.currentTimeMillis());
                tagValue.setTagKey(tagKey);
                tagValue.setTagValue(data[1]);
                tagValue.setTagValueAlias(data[2]);
                tagValue.setTagId(tagId);

                TagValueExample example = new TagValueExample();
                example.createCriteria().andTagKeyEqualTo(tagKey).andTagValueEqualTo(data[1]).andTagIdEqualTo(tagId);

                if (isClear) {
                    tagValue.setId(UUIDUtil.newUUID());
                    tagValueMapper.deleteByExample(example);
                    tagValueMapper.insert(tagValue);
                } else if (tagValueMapper.countByExample(example) > 0) {
                    tagValueMapper.updateByExampleSelective(tagValue, example);
                } else {
                    tagValue.setId(UUIDUtil.newUUID());
                    tagValueMapper.insert(tagValue);
                }
                count++;
                line = reader.readLine();
            }
        } catch (Exception e) {
            throw e;
        }

        return count;
    }
}
