package com.fit2cloud.commons.server;

import com.fit2cloud.commons.server.base.domain.CloudServerCredential;
import com.fit2cloud.commons.server.config.BeforeTest;
import com.fit2cloud.commons.server.model.CloudServerDTO;
import com.fit2cloud.commons.server.service.CloudServerCommonService;
import com.fit2cloud.commons.server.service.CloudServerCredentialService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CloudServerServiceTest extends BeforeTest {
    @Resource
    private CloudServerCommonService cloudServerService;
    @Resource
    private CloudServerCredentialService cloudServerCredentialService;

    @Value("${prometheus.push-gateway.clear-period:25}")
    private long period;

    @Test
    public void testCloudServerService() {
        Map<String, Object> params = new HashMap<>();
        List<CloudServerDTO> cloudServers = cloudServerService.selectCloudServerList(params);
        System.out.println(cloudServers.size());
    }

    @Test
    public void testCloudServerCredentialService() {
        List<CloudServerCredential> cloudServerCredentialList = cloudServerCredentialService.getCloudServerCredentialList("c67334ce-ad98-4928-8c67-d5155d415adc");
        System.out.println(cloudServerCredentialList);
    }

    @Test
    public void test1() {
        System.out.println(period);
    }

}
