package com.fit2cloud.mc.utils;

import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.config.InternalDockerRegistry;
import com.fit2cloud.mc.dto.ModuleParamData;
import com.google.gson.Gson;
import com.sun.org.apache.xml.internal.utils.NameSpace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class K8sUtil {

    private static String helm = "/usr/bin/helm";
    private static String chartFile = "Chart.yaml";
    private static String valueFile = "values.yaml";


    public static void startService(String serviceName) throws Exception{

    }

    public static void stopService(String serviceName)throws Exception{
        List<String> command = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        if(!checkServiceExist(serviceName, command, result)){
            LogUtil.info("Service {} do not exist.", serviceName);
            return;
        }
        result.setLength(0);
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

    public static void startService(String serviceName, ModuleParamData moduleParamData, Map<String, Object> params){
        Integer podNumber = params.get("pod_number") == null ? 1 : Integer.valueOf(params.get("pod_number").toString());
        KubernetesClient client = new DefaultKubernetesClient();
        NamespaceList namespaceList = client.namespaces().list();
        System.out.printf("namespaceList: " + new Gson().toJson(namespaceList));

        moduleParamData.getDeployment().forEach(deployment -> {
            client.apps().deployments().withName(deployment).edit().buildSpec().setReplicas(podNumber);
        });
    }

    public static void stopService(String serviceName, ModuleParamData moduleParamData){
        KubernetesClient client = new DefaultKubernetesClient();
        moduleParamData.getDeployment().forEach(deployment -> {
            Integer podNumber = 0;
            client.apps().deployments().withName(deployment).edit().buildSpec().setReplicas(podNumber);
        });
    }

    public static ModuleParamData installOrUpdateModule(String serviceName, String moduleFileName, boolean onLine, Map<String, Object> params) throws Exception {
        List<String> command = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        String random_dir_name = UUID.randomUUID().toString();
        String tmp_dir="/tmp/f2c-extensions/" + random_dir_name;
        String chartsDir = tmp_dir + "/helm-charts/";
        String templatesDir = "templates/";

        if(checkServiceExist(serviceName, command, result)){
            stopService(serviceName);
        }
        result.setLength(0);

        try{
            uncompress(moduleFileName, tmp_dir);
        }catch (Exception e) {
            LogUtil.error("Filed to uncompress module file " + e.getMessage());
            throw new Exception(e.getMessage());
        }

        checkFileExist(chartsDir,  chartFile);
        checkFileExist(chartsDir,  valueFile);

        LogUtil.info("Begin start service " +  serviceName);
        command.add(helm);
        command.add("install");
        command.add(serviceName);
        if(!onLine){
            InternalDockerRegistry internalDockerRegistry = CommonBeanFactory.getBean(InternalDockerRegistry.class);
            command.add("--set");
            command.add("modules.imagePrefix=" + internalDockerRegistry.getImagePrefix());
        }else {
            //在线环境，不需要imagepullsecret
        }

        if(params.get("pod_number") != null){
            Integer podNumber = Integer.valueOf(params.get("pod_number").toString());
            //指定pod数量
            handleReplicas(podNumber, chartsDir + valueFile);
        }

        command.add(chartsDir);
        int starCode = execCommand(result, command);
        if(starCode != 0){
            LogUtil.error("Start service failed: " + result.toString());
            throw new Exception("Start service failed: " + result.toString());
        }else {
            LogUtil.info("Success to Start service: " + result.toString());
        }
        result.setLength(0);
        ModuleParamData moduleParamData = new ModuleParamData();
        moduleParamData.setDeployment(filterDeployments(chartsDir + templatesDir, command, result));
        return moduleParamData;
    }

    private static List<String> filterDeployments(String path, List<String> command, StringBuilder result) throws Exception{
        List<String> deployments = new ArrayList<>();
        try {

        }catch (Exception e){

        }
        String commands[]={"find", path,"-name", "\"*.yaml\"", "|", "xargs", "grep", "Deployment", "|", "awk", "-F", ":", "'{print $1}'"};
        List<String> commandsList=Arrays.asList(commands);
        command.addAll(commandsList);
        int starCode = execCommand(result, command);
        if(starCode == 0){
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(result.toString().getBytes(Charset.forName("utf8")))));
            String line;
            while ((line=bufferedReader.readLine()) != null){
                deployments.addAll(filterDeployments(path + line));
            }
        }
        result.setLength(0);
        return deployments;
    }

    private static List<String> filterDeployments(String valueYamlFile) throws Exception{
        List<String> deployments = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(valueYamlFile));
        String line;
        while ((line = bufferedReader.readLine()) != null){
            if(line.contains("kind:") && line.contains("Deployment")){
                if((line = bufferedReader.readLine()) != null && line.contains("metadata")){
                    if((line = bufferedReader.readLine()) != null && line.contains("name:")){
                        deployments.add(line.replace("name:", "").replace(" ", ""));
                    }
                }
            }
            if(line == null) break;
        }
        return deployments;
    }

    private static void handleReplicas(Integer replicas, String valueYamlFile) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(valueYamlFile));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line=bufferedReader.readLine()) != null){
            if(line.contains("replicas:")){
                String oldreplicas = line.replace("replicas:", "").replace(" ", "");
                line = line.replace(oldreplicas, replicas.toString());
            }
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(valueYamlFile));
        bufferedWriter.write(stringBuilder.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }




    private static boolean checkServiceExist(String serviceName, List<String> command, StringBuilder result)throws Exception{
        command.add(helm);
        command.add("list");
        int starCode = execCommand(result, command);
        if(starCode == 0 && StringUtils.isNotEmpty(result.toString()) && result.toString().contains(serviceName)){
            return true;
        }
        return false;
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
        command.clear();
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
