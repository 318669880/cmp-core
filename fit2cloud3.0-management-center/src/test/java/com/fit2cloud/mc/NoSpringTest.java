package com.fit2cloud.mc;

import com.fit2cloud.mc.config.DockerRegistry;
import com.fit2cloud.mc.utils.K8sUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: chunxing
 * Date: 2018/7/3  下午6:56
 * Description:
 */
public class NoSpringTest {

    public static void main(String[] args) throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        DockerRegistry dockerRegistry = new DockerRegistry();
        dockerRegistry.setUrl("tjlygdx@163.com");
        dockerRegistry.setUser("tjlygdx@163.com");
        dockerRegistry.setPasswd("Fit2cloud!");
        String authStr = String.format("%s:%s", dockerRegistry.getUser(), dockerRegistry.getPasswd());
        String enStr = encoder.encodeToString(authStr.getBytes());
        String dataStr =  String.format("{\"auths\":{\"%s\":{\"username\":\"%s\",\"password\":\"%s\",\"auth\":\"%s\"}}}",
                dockerRegistry.getUrl(), dockerRegistry.getUser(), dockerRegistry.getPasswd(), enStr);
        dataStr = encoder.encodeToString(dataStr.getBytes());
        System.out.println(dataStr);
    }
}