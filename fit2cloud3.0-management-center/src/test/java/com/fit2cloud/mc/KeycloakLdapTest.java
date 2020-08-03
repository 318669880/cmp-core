package com.fit2cloud.mc;

import com.fit2cloud.commons.server.base.domain.ExtraUser;
import com.fit2cloud.commons.server.base.mapper.ExtraUserMapper;
import com.fit2cloud.mc.service.ExtraUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Author: chunxing
 * Date: 2018/6/22  下午2:45
 * Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class KeycloakLdapTest {

    @Resource
    private ExtraUserMapper extraUserMapper;

    @Resource
    private ExtraUserService extraUserService;

    @Test
    public void test2() {
        extraUserService.syncExtraUser(true);
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    @Rollback(value = false)
    public void test3() {
        ExtraUser extraUser1 = new ExtraUser();
        extraUser1.setId("xxxxx");
        extraUserMapper.insert(extraUser1);
        try {
            ExtraUser extraUser = new ExtraUser();
            extraUser.setId("uuuu");
            extraUserMapper.insert(extraUser);
        } catch (Exception e) {
            throw e;
        }
    }

}
