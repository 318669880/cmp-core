package com.fit2cloud.plugin.container.sdk.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UnitUtil {

    /**
     * amount为带单位的字符串，例如500Gi
     *
     * @param amount 数量
     * @return 单位换算为Gi后的浮点数(保留2位小数)
     */
    public static float getGi(String amount) {
        if (amount == null) {
            return 0;
        }
        if (!amount.contains("i")) {
            return Float.valueOf(amount);
        }
        BigDecimal number = new BigDecimal(amount.substring(0, amount.length() - 2));
        String unit = amount.substring(amount.length() - 2);
        switch (unit) {
            case "Ti":
                number = number.multiply(new BigDecimal(1024));
                break;
            case "Mi":
                number = number.divide(new BigDecimal(1024), 2, RoundingMode.HALF_UP);
                break;
            case "Ki":
                number = number.divide(new BigDecimal(1048576), 2, RoundingMode.HALF_UP);
                break;
        }
        return number.floatValue();
    }

    public static float getCPU(String amount) {
        if (amount == null) {
            return 0;
        }
        if (!amount.toLowerCase().contains("m")) {
            return Float.valueOf(amount);
        }

        BigDecimal number = new BigDecimal(amount.substring(0, amount.length() - 1));
        BigDecimal result = number.divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);
        return result.floatValue();
    }
}
