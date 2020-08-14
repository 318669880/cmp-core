package com.fit2cloud.mc;

import com.fit2cloud.mc.config.BeforeTest;
import com.fit2cloud.mc.service.ModelManagerService;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests extends BeforeTest {
    /*@Resource
    private ServerInfo serverInfo;

    @Test
    public void contextLoads() {
        String module = serverInfo.getModule().getId();
        List<Menu> menuList = serverInfo.getMenuList();
        List<Permission> permissionList = serverInfo.getPermissionList();
    }*/

    private ModelManagerService modelManagerService;



}


