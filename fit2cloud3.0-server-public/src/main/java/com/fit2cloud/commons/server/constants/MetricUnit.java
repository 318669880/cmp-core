package com.fit2cloud.commons.server.constants;

public enum MetricUnit {
    PERCENT("%"),
    KBPS("kb/s"),
    OPS("ops/s"),
    GB("GB"),
    MB("MB"),
    BYTE("Bytes"),
    KB("KB"),
    MHz("MHz"),
    MS("ms"),
    BPS("bytes/s"),
    NONE("N/A");
    private String unit;

    MetricUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return this.unit;
    }
}
