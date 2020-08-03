package com.fit2cloud.mc;

import com.fit2cloud.mc.service.SystemParameterService;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Author: chunxing
 * Date: 2018/7/18  下午6:44
 * Description:
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailTest {
    @Resource
    private SystemParameterService parameterService;

}
