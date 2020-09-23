package com.fit2cloud.sdk.util;

public class Translator {
    private static final String PREFIX = "$[{";
    private static final String SUFFIX = "}]";

    public static String toI18nKey(String key) {
        return String.format("%s%s%s", PREFIX, key, SUFFIX);
    }

}
