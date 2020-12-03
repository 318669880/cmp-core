package com.fit2cloud.commons.server.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.FlowNotificationConfig;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author gin
 * @Date 2020/12/3 3:02 下午
 */
public class ProcessText {
    public static String parseText(FlowNotificationConfig config, Map<String, Object> params) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        String simpleContent = JSONObject.parseObject(config.getTemplate()).getString("simpleContent");
        if (StringUtils.isEmpty(simpleContent)) {
            return "";
        }
        String[] split = simpleContent.split("\n");
        String instances = split[0].replace("{", "").replace("}", "");
        String text = simpleContent.replace("{" + split[0] + "}", "");
        Object o = params.get(instances);
        JSONArray jsonArray = JSONObject.parseArray(new Gson().toJson(o));
        for (int i = 0; i < jsonArray.size(); i++) {
            Object ins = jsonArray.get(i);
            String content = getContent(text, objectToMap(ins));
            stringBuffer.append(content).append("\n\n");
        }
        return stringBuffer.toString();
    }

    private static String getContent(String tempalte, Map<String, Object> parameters) {
        Pattern p = Pattern.compile("(\\{([a-zA-Z]+)\\})");
        Matcher m = p.matcher(tempalte);
        StringBuffer stringBuffer = new StringBuffer();
        while (m.find()) {
            String key = m.group(2);
            String value = null;
            if (parameters.containsKey(key)) {
                value = (String) parameters.get(key);
            }
            value = (value == null) ? "" : value;
            m.appendReplacement(stringBuffer, value);
        }
        m.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null)
            return null;
        Map<String, Object> map = new HashMap<String, Object>();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = getter != null ? getter.invoke(obj) : null;
            map.put(key, value);
        }
        return map;
    }
}
