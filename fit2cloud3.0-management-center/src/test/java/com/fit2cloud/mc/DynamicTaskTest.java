package com.fit2cloud.mc;

import com.fit2cloud.mc.config.BeforeTest;
import com.fit2cloud.mc.job.DynamicTaskJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledFuture;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/10/20 2:26 下午
 * @Description: Please Write notes scientifically
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan("com.fit2cloud.mc.*")
public class DynamicTaskTest extends BeforeTest {

    @Resource
    private DynamicTaskJob dynamicTaskJob;

    @Test
    public void test(){

        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        ScheduledFuture<?> future = dynamicTaskJob.add(() -> {
            String format = LocalDateTime.now().format(ofPattern);
            System.out.println(format);
        }, "0/5 * * * * *");

        try{
            Thread.sleep(30000);
            dynamicTaskJob.delete(future);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void testWithOutTime() throws Exception{
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        dynamicTaskJob.addTaskWithTime(() -> {
            String format = LocalDateTime.now().format(ofPattern);
            System.out.println(format);
        }, "0/5 * * * * *", 15000L);

    }
}
