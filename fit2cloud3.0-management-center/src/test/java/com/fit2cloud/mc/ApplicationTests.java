package com.fit2cloud.mc;

import com.fit2cloud.commons.server.model.Menu;
import com.fit2cloud.commons.server.model.Permission;
import com.fit2cloud.commons.server.module.ServerInfo;
import com.fit2cloud.mc.config.BeforeTest;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.strategy.service.ModelOperateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


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


