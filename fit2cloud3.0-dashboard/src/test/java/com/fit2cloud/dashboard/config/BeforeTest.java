package com.fit2cloud.dashboard.config;

import org.junit.BeforeClass;

public class BeforeTest {
    @BeforeClass
    public static void setSystemProperties() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
