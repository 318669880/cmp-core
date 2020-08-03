package com.fit2cloud.mc.utils;

import com.fit2cloud.commons.server.i18n.Translator;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleUtils {

    private final static Map<String, String> SYSTEM_ROLE_LIST = new HashMap<>();

    static {
        SYSTEM_ROLE_LIST.put("系统管理员", "ADMIN");
        SYSTEM_ROLE_LIST.put("组织管理员", "ORGADMIN");
        SYSTEM_ROLE_LIST.put("工作空间用户", "USER");
    }

    public static List<String> getSystemRoleIds(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        List<String> resultList = new ArrayList<>();

        name = StringUtils.replace(name, "%", "");

        for (String key : SYSTEM_ROLE_LIST.keySet()) {
            if (StringUtils.contains(Translator.get(key), name)) {
                resultList.add(SYSTEM_ROLE_LIST.get(key));
            }
        }
        return resultList;
    }
}
