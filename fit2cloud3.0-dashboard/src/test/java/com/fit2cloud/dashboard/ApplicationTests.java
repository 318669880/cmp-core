package com.fit2cloud.dashboard;

import com.fit2cloud.dashboard.config.BeforeTest;
import com.fit2cloud.dashboard.service.DashboardService;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests extends BeforeTest {
    @Resource
    private DashboardService dashboardService;


//    @Test
//    public void context() {
//        dashboardService.customize(null);
//    }
}


