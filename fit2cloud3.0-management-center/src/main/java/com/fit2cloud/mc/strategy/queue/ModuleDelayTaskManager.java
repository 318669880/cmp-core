package com.fit2cloud.mc.strategy.queue;

import com.fit2cloud.mc.strategy.service.EurekaCheckService;
import com.fit2cloud.mc.strategy.task.ModelNodeTask;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/13 5:05 下午
 * @Description: Please Write notes scientifically
 * 延迟任务
 *
 */

@Component
public class ModuleDelayTaskManager implements ApplicationRunner {

    private DelayQueue<DelayTask> delayQueue = new DelayQueue<DelayTask>();

    public void addTask(long time,Consumer consumer,Object target){
        delayQueue.add(new DelayTask(time,consumer,target));
    }

    @Resource
    private ModelNodeTask modelNodeTask;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Executors.newSingleThreadExecutor().execute(new Thread(this::excuteThread));
        modelNodeTask.registerCurrentMc();
        //modelNodeTask.joinEurekaCluster();
    }

    private void excuteThread() {
        while (true) {
            //if(delayQueue.size()==0) continue;
            try {
                DelayTask task = delayQueue.take();
                task.getConsumer().accept(task.getTarget());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
