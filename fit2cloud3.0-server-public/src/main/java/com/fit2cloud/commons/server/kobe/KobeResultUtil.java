package com.fit2cloud.commons.server.kobe;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Author gin
 * @Date 2020/9/21 10:35 上午
 */
public class KobeResultUtil {
    public static String getStdOut(Kobe.GetResultResponse resultResponse) {
        JSONObject jsonHost = getJsonHost(resultResponse);
        if (jsonHost != null) {
            JSONArray stdout_lines = jsonHost.getJSONArray("stdout_lines");
            if (stdout_lines != null && stdout_lines.size() > 0) {
                StringBuilder buffer = new StringBuilder();
                for (int i = 0; i < stdout_lines.size(); i++) {
                    buffer.append(stdout_lines.getString(i)).append("\n");
                }
                return buffer.toString();
            }
        }
        return "";
    }

    public static String getMsg(Kobe.GetResultResponse resultResponse) {
        JSONObject jsonHost = getJsonHost(resultResponse);
        if (jsonHost != null) {
            String msg = jsonHost.getString("msg");
            if (msg != null) {
                return msg + "\n";
            }
        }
        return "";
    }

    private static JSONObject getJsonHost(Kobe.GetResultResponse resultResponse) {
        String content = resultResponse.getItem().getContent();
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONArray plays = jsonObject.getJSONArray("plays");
        if (plays != null && plays.size() > 0) {
            JSONObject jsonObject1 = plays.getJSONObject(0);
            JSONArray tasks = jsonObject1.getJSONArray("tasks");
            if (tasks != null && tasks.size() > 0) {
                JSONObject jsonObject2 = tasks.getJSONObject(0);
                JSONObject hosts = jsonObject2.getJSONObject("hosts");
                JSONObject host = hosts.getJSONObject("default-host");
                return host;
            }
        }
        return null;
    }
}
