package com.fit2cloud.mc.strategy.queue;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/13 5:17 下午
 * @Description: Please Write notes scientifically
 */
public class DelayTask implements Delayed {

    private Long delayTime;
    private Consumer consumer;
    private Object target;
    private Long createTime;

    private Long cancelTime;



    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(  this.createTime + delayTime -System.currentTimeMillis(),TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        DelayTask delayTask = (DelayTask) o;
        return this.cancelTime.compareTo(delayTask.getCancelTime());
    }



    public DelayTask(Long delayTime, Consumer consumer,Object target) {
        this.delayTime = delayTime;
        this.consumer = consumer;
        this.target = target;
        this.createTime = System.currentTimeMillis();
        this.cancelTime = this.createTime + this.delayTime;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public Object getTarget() {
        return target;
    }

    public Long getCancelTime() {
        return cancelTime;
    }
}
