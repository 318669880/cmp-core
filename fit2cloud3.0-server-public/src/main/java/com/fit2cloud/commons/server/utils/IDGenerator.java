package com.fit2cloud.commons.server.utils;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class IDGenerator {

    private final static String SEPARATOR = "-";

    private static String get(String prefix) {
        StringBuilder stringBuilder = new StringBuilder();
        String dateStr = DateFormatUtils.format(DateUtils.truncate(new Date(), Calendar.MILLISECOND), "yyyyMMddHHmm", TimeZone.getTimeZone("GMT+08:00"));
        stringBuilder.append(prefix).append(SEPARATOR).append(dateStr).append(SEPARATOR);
        stringBuilder.append(UUID.randomUUID().toString().substring(0, 8).toLowerCase());
        return stringBuilder.toString();
    }

    public static String newBusinessId(String prefix, String... workspaceId) {
        return get(prefix);
    }

}
