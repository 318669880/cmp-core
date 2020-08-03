package com.fit2cloud.commons.server.utils;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.base.mapper.SystemParameterMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Author: chunxing
 * Date: 2018/6/28  上午11:02
 * Description:
 */
@Component
public class ParamUtils {

    private static SystemParameterMapper systemParameterMapper;

    @Resource
    public void setSystemParameterMapper(SystemParameterMapper systemParameterMapper) {
        ParamUtils.systemParameterMapper = systemParameterMapper;
    }

    /**
     * 根据，key,获取 value，并转换为指定类型。
     */
    public static <T> T getVule(String key, Class<T> objectClass) throws IllegalAccessException, InstantiationException {
        SystemParameter parameter = systemParameterMapper.selectByPrimaryKey(key);
        if (parameter == null) {
            return objectClass.newInstance();
        } else {
            return JSON.parseObject(parameter.getParamValue(), objectClass);
        }
    }
}
