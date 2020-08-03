package com.fit2cloud.commons.server.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ProcessEmail {

    static String[] getReceivers(JSONObject template, Map<String, Object> map) {
        Set<String> set = new HashSet<>();
        JSONArray receivers = template.getJSONArray("receivers");
        for (Object receiver : receivers) {
            if (((JSONObject) receiver).getBooleanValue("checked")) {
                String key = ((JSONObject) receiver).getString("key");
                if (StringUtils.equals(key, "OTHER")) {
                    set.addAll(template.getJSONArray("others").toJavaList(String.class));
                } else {
                    if (map.get(key) != null) {
                        Collections.addAll(set, StringUtils.split(map.get(key).toString(), ","));
                    }
                }
            }
        }
        return set.stream().distinct().toArray(String[]::new);
    }

    static String getContent(int configId, JSONObject template, Map<String, Object> map) {
        String content = template.getString("content");
        String result;
        try {
            result = TemplateUtils.merge(content, map);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error("格式化邮件内存出错, 消息配置ID:" + configId);
            result = "";
        }
        return result;
    }
}
