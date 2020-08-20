package com.fit2cloud.mc.utils;

import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.config.InternalDockerRegistry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class HelmUtil {

    private static String helm = "/usr/bin/helm";
    private static String chartFile = "Chart.yaml";

    public static void startService(String serviceName) throws Exception{

    }

    public static void stopService(String serviceName)throws Exception{
        List<String> command = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        LogUtil.info("Begin stop service " +  serviceName);
        command.add(helm);
        command.add("uninstall");
        command.add(serviceName);
        int starCode = execCommand(result, command);
        if(starCode != 0){
            LogUtil.error("stop service failed: " + result.toString());
            throw new Exception("stop service failed: " + result.toString());
        }else {
            LogUtil.info("Success to stop service: " + result.toString());
        }
        result.setLength(0);
    }

    public static void installOrUpdateModule(String serviceName, String moduleFileName, boolean onLine, Map<String, Object> params) throws Exception {
        List<String> command = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        String random_dir_name = UUID.randomUUID().toString();
        String tmp_dir="/tmp/f2c-extensions/" + random_dir_name;
        String chartsDir = tmp_dir + "/helm-charts/";

        try{
            uncompress(moduleFileName, tmp_dir);
        }catch (Exception e) {
            LogUtil.error("Filed to uncompress module file " + e.getMessage());
            throw new Exception(e.getMessage());
        }

        checkFileExist(chartsDir,  chartFile);

        if(!onLine){
           //TODO
        }

        LogUtil.info("Begin start service " +  serviceName);
        command.add(helm);
        command.add("upgrade");
        command.add(serviceName);
        command.add("--install");
        command.add("--recreate-pods");
        command.add(chartsDir);
        int starCode = execCommand(result, command);
        if(starCode != 0){
            LogUtil.error("Start service failed: " + result.toString());
            throw new Exception("Start service failed: " + result.toString());
        }else {
            LogUtil.info("Success to Start service: " + result.toString());
        }
        result.setLength(0);

    }

    private static void uncompress(String moduleFileName, String targetDir) throws Exception{
        File file = new File(targetDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        LogUtil.info("Begin to uncompress module file.");
        File sourceFile = new File(moduleFileName);
        if(!sourceFile.exists() && !sourceFile.isFile()){
            LogUtil.error("Module file: " + moduleFileName + " is not exist or is not file.");
            throw new Exception("Module file: " + moduleFileName + " is not exist or is not file.");
        }
        TarArchiveInputStream fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(moduleFileName)));
        File extraceFolder = new File(targetDir);
        TarArchiveEntry entry;
        while ((entry = fin.getNextTarEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(extraceFolder, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            IOUtils.copy(fin, new FileOutputStream(curfile));
        }
        LogUtil.info("End of uncompress module file.");
    }

    private static void checkFileExist(String path, String fileName) throws Exception{
        File file = new File(path + fileName);
        if(!file.exists() || !file.isFile()){
            LogUtil.error("Cannot find {} in {}." , fileName, path);
            throw new Exception("Cannot find " + path + fileName );
        }
    }

    private static int execCommand(StringBuilder result, List<String> command) throws Exception{
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        Process process = builder.start();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line=bufferedReader.readLine()) != null){
            result.append(line).append("\n");
        }
        int exitCode = process.waitFor();
        return exitCode;
    }

    private static String filterChartName(String chartFile) throws Exception{
        Map chart = new Yaml().load(new FileInputStream(new File(chartFile)));
        Object serviceName =  chart.get("name");
        if(serviceName == null){
            LogUtil.error("Filed to judge service name from " + chartFile);
            throw new Exception("Filed to judge service name from " + chartFile);
        }else{
            return serviceName.toString();
        }
    }

}
