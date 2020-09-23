package com.fit2cloud.plugin.container.sdk.utils;

import java.util.Optional;

public class NumberUtil {

    private static final int ZERO = 0;

    public static int convert(Integer integer) {
        return Optional.ofNullable(integer).orElse(ZERO);
    }
}
