package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.base.domain.SystemParameterExample;
import com.fit2cloud.commons.server.base.mapper.SystemParameterMapper;
import com.fit2cloud.commons.server.constants.ParamConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: chunxing
 * Date: 2018/7/18  下午2:50
 * Description:
 */
@Service
public class ParameterCommonService {

    @Resource
    private SystemParameterMapper parameterMapper;

    public List<SystemParameter> uiInfo() {
        SystemParameterExample example = new SystemParameterExample();
        example.createCriteria().andParamKeyLike(ParamConstants.Classify.UI.getValue() + "%");
        return parameterMapper.selectByExample(example);
    }

    public String getSystemLanguage() {
        String result = StringUtils.EMPTY;
        SystemParameterExample example = new SystemParameterExample();
        example.createCriteria().andParamKeyEqualTo(ParamConstants.UI.LANGUAGE.getValue());
        List<SystemParameter> list = parameterMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            String value = list.get(0).getParamValue();
            if (StringUtils.isNotBlank(value)) {
                result = value;
            }
        }
        return result;
    }
}
