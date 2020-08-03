package com.fit2cloud.commons.pluginmanager;

import com.fit2cloud.sdk.ICloudProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PluginTest {
    @Autowired
    private CloudProviderManager cloudProviderManager;


    @Test
    public void test2() {
        ICloudProvider cloudProvider = cloudProviderManager.getCloudProvider("fit2cloud-vsphere-plugin");
        System.out.println(cloudProvider);
    }
}
