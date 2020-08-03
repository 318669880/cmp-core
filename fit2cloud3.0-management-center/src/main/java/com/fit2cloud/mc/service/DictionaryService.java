package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.base.domain.Dictionary;
import com.fit2cloud.commons.server.base.domain.DictionaryExample;
import com.fit2cloud.commons.server.base.mapper.DictionaryMapper;
import com.fit2cloud.commons.server.constants.CategoryConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.dto.DictionaryOsDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DictionaryService {

    @Resource
    private DictionaryMapper dictionaryMapper;

    public List<DictionaryOsDTO> getOsCategoryList() {
        DictionaryExample dictionaryExample = new DictionaryExample();
        DictionaryExample.Criteria criteria = dictionaryExample.createCriteria();
        criteria.andCategoryEqualTo(CategoryConstants.vm_os.name());
        List<Dictionary> list = dictionaryMapper.selectByExample(dictionaryExample);
        List<DictionaryOsDTO> result = new ArrayList<>();
        for (Dictionary dictionary : list) {
            DictionaryOsDTO dto = new DictionaryOsDTO();
            BeanUtils.copyBean(dto, dictionary);
            DictionaryExample example = new DictionaryExample();
            example.createCriteria().andCategoryEqualTo(CategoryConstants.vm_os_version.name())
                    .andDictionaryKeyEqualTo(dictionary.getDictionaryKey());
            example.setOrderByClause("dictionary_value");
            dto.setVersionList(dictionaryMapper.selectByExample(example));
            result.add(dto);
        }
        return result;
    }

    public void addOrEditOsCategory(Dictionary dictionary, boolean add) {
        if (StringUtils.isEmpty(dictionary.getDictionaryKey()) ||
                StringUtils.isEmpty(dictionary.getDictionaryValue())) {
            F2CException.throwException(Translator.get("i18n_ex_dictionary_key_value"));
        }
        DictionaryExample dictionaryExample = new DictionaryExample();
        DictionaryExample.Criteria criteria = dictionaryExample.createCriteria();
        criteria.andCategoryEqualTo(CategoryConstants.vm_os.name())
                .andDictionaryKeyEqualTo(dictionary.getDictionaryKey());
        if (!add) {
            if (StringUtils.isEmpty(dictionary.getId())) {
                F2CException.throwException(Translator.get("i18n_ex_dictionary_id"));
            }
            criteria.andIdNotEqualTo(dictionary.getId());
        }
        List<Dictionary> dictionaries = dictionaryMapper.selectByExample(dictionaryExample);
        if (!CollectionUtils.isEmpty(dictionaries)) {
            F2CException.throwException(Translator.get("i18n_ex_dictionary_key_exist"));
        }

        if (add) {
            dictionary.setId(UUIDUtil.newUUID());
            dictionary.setCategory(CategoryConstants.vm_os.name());
            dictionaryMapper.insertSelective(dictionary);
        } else {
            dictionaryMapper.updateByPrimaryKey(dictionary);
        }
    }

    public void deleteOsCategory(String id) {
        Dictionary dictionary = dictionaryMapper.selectByPrimaryKey(id);
        if (dictionary != null) {
            DictionaryExample dictionaryExample = new DictionaryExample();
            dictionaryExample.createCriteria().andCategoryEqualTo(CategoryConstants.vm_os_version.name())
                    .andDictionaryKeyEqualTo(dictionary.getDictionaryKey());
            dictionaryMapper.deleteByExample(dictionaryExample);
            dictionaryMapper.deleteByPrimaryKey(id);
        }
    }

    public void addOsVersion(String osId, String version) {
        Dictionary dictionary = dictionaryMapper.selectByPrimaryKey(osId);

        DictionaryExample dictionaryExample = new DictionaryExample();
        dictionaryExample.createCriteria().andCategoryEqualTo(CategoryConstants.vm_os_version.name())
                .andDictionaryKeyEqualTo(dictionary.getDictionaryKey())
                .andDictionaryValueEqualTo(version);

        List<Dictionary> list = dictionaryMapper.selectByExample(dictionaryExample);

        if (!CollectionUtils.isEmpty(list)) {
            F2CException.throwException(Translator.get("i18n_ex_dictionary_version_exist"));
        }

        Dictionary versionDic = new Dictionary();
        versionDic.setId(UUIDUtil.newUUID());
        versionDic.setCategory(CategoryConstants.vm_os_version.name());
        versionDic.setDictionaryKey(dictionary.getDictionaryKey());
        versionDic.setDictionaryValue(version);
        dictionaryMapper.insert(versionDic);

    }

    public void deleteOsVersion(String id) {
        dictionaryMapper.deleteByPrimaryKey(id);
    }
}
