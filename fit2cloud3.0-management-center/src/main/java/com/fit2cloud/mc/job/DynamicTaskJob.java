package com.fit2cloud.mc.job;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.concurrent.ScheduledFuture;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/10/20 2:19 下午
 * @Description: Please Write notes scientifically
 */

@Service
public class DynamicTaskJob {



    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public ScheduledFuture<?> add(Runnable runnable, String cron){
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule( runnable , new CronTrigger(cron));
        return future;
    }

    public void delete(ScheduledFuture<?> future){
        future.cancel(true);
    }

    public void addTaskWithTime(Runnable runnable, String cron, Long outTime) throws Exception{
        ScheduledFuture<?> future = add(runnable, cron);
        Thread.sleep(outTime);
        delete(future);
    }

}
