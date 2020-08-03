package com.fit2cloud.commons.server.utils;

import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class MetricUtils {
    public static void fillSyncDate(long syncTime, Object metric) {
        Map<String, String> dateMap = getSyncDate(syncTime);

        invokeMethod(metric, "setSyncYear", dateMap.get("syncYear"));
        invokeMethod(metric, "setSyncMonth", dateMap.get("syncMonth"));
        invokeMethod(metric, "setSyncWeek", dateMap.get("syncWeek"));
        invokeMethod(metric, "setSyncDay", dateMap.get("syncDay"));
        invokeMethod(metric, "setSyncHour", dateMap.get("syncHour"));
        invokeMethod(metric, "setSyncMin", dateMap.get("syncMin"));
        invokeMethod(metric, "setLastSyncTimestamp", syncTime);
    }


    public static Optional<Method> getMethod(Object object, String methodName, Object... params) {
        try {
            return Optional.of(object.getClass().getMethod(methodName, ClassUtils.toClass(params)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static void invokeMethod(Object object, String methodName, Object... params) {
        getMethod(object, methodName, params).ifPresent(method -> {
            try {
                MethodUtils.invokeMethod(object, true, methodName, params);
            } catch (Exception e) {
                LogUtil.error("方法调用失败:{}", ExceptionUtils.getStackTrace(e));
            }
        });
    }


    public static void setMetricValue(Object metricValue, String metricName, Object metric) {
        Double realValue = Optional.ofNullable(metricValue)
                .map(value -> Double.valueOf(metricValue.toString()))
                .orElse(0.0);
        invokeMethod(metric, "setMetricName", metricName);
        // 插件里的使用率是100*实际使用率，这里还原成原始值
        if (StringUtils.equalsAny(metricName, "CpuUsage", "MemoryUsage")) {
            realValue = realValue / 100.0;
        }

        if(Double.isNaN(realValue)) {
            realValue = 0.0;
        }
        invokeMethod(metric, "setValue", realValue);
    }

    public static long getPerMetricSyncTimePoint(long timeInMillis) {
        try {
            ZonedDateTime utc = Instant.ofEpochMilli(timeInMillis).atZone(ZoneId.of("UTC"));
            int min = utc.getMinute();
            int newMin = (min + 2) / 5 * 5;
            // 分钟计算有可能超过59
            newMin = newMin > 59 ? 59 : newMin;
            utc = utc.withMinute(newMin).withSecond(0).withNano(0);
            return utc.toInstant().toEpochMilli();
        } catch (Exception pe) {
            LogUtil.error("When getting the time point, an error happern.", pe);
        }
        return timeInMillis;
    }

    public static Map<String, String> getSyncDate(long syncTime) {
        Date syncDate = new Date(syncTime);
        TimeZone timeZone = TimeZone.getTimeZone("GMT+08:00");
        Calendar cal = Calendar.getInstance();
        cal.setTime(syncDate);
        String syncYear = DateFormatUtils.format(cal, "yyyy", timeZone);
        String syncMonth = DateFormatUtils.format(cal, "yyyy-MM", timeZone);
        String syncDay = DateFormatUtils.format(cal, "yyyy-MM-dd", timeZone);
        String syncHour = DateFormatUtils.format(cal, "yyyy-MM-dd HH", timeZone);
        String syncMin = DateFormatUtils.format(cal, "yyyy-MM-dd HH:mm", timeZone);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return new HashMap<String, String>() {{
            put("syncYear", syncYear);
            put("syncMonth", syncMonth);
            put("syncWeek", syncYear + "-" + cal.get(Calendar.WEEK_OF_YEAR));
            put("syncDay", syncDay);
            put("syncHour", syncHour);
            put("syncMin", syncMin);
        }};
    }
}
