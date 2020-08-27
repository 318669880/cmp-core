package com.fit2cloud.mc;

import com.fit2cloud.mc.utils.K8sUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
        List<String> deployments = new ArrayList<>();
        List<String> deployment = K8sUtil.filterDeployments(deployments, "/tmp/f2c-extensions/e2bc819b-68e4-47c0-8913-f6edb719ee16/helm-charts/templates");
        System.out.println(deployment.size());
//        Double sizeCount = 0.0;
//        List<String> lines = FileUtils.readLines(new File("/opt/fit2cloud/conf/disk"));
//
//        String rex = "\\S[0-9][^ ].+";
//        Pattern pattern = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
//        List<String> dirs = Arrays.asList("/", "/home", "/boot");
//        for (String line : lines) {
//            String[] split = line.split("%");
//            if (split.length == 1 || dirs.contains(split[1].trim())) {
//                Matcher matcher = pattern.matcher(line);
//                if (matcher.find()) {
//                    String sizes = matcher.group().replaceAll("\\s+", " ");
//                    String[] strs = sizes.split(" ");
//                    sizeCount += Double.valueOf(strs[1].trim()) / 1024 / 1024;
//                }
//            }
//        }
//        System.out.println("end");
    }
}