package com.fit2cloud.commons.server;

import com.fit2cloud.commons.server.utils.MetricUtils;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

public class MethodTest {
    @Test
    public void test1() throws Exception {

    }

    @Test
    public void testInstant() {
        long timeInMillis = System.currentTimeMillis();
        long perMetricSyncTimePoint = MetricUtils.getPerMetricSyncTimePoint(timeInMillis);
        System.out.println(timeInMillis);
        System.out.println(perMetricSyncTimePoint);
    }


    @Test
    public void testTimer() throws InterruptedException {
        Timer timer = new Timer();
        for (int i = 0; i < 2000; i++) {
            int finalI = i;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("hello world " + finalI);
                }
            }, 10 * 1000);
        }

        Thread.sleep(100 * 1000);
    }
}
