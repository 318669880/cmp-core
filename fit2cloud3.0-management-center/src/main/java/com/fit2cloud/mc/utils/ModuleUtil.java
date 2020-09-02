package com.fit2cloud.mc.utils;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.config.DockerRegistry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModuleUtil {

    private static String workDir="/opt/fit2cloud";
    private static String moduleActionInstall = "install";
    private static String moduleActionUpdate = "update";
    private static String docker = "/usr/bin/docker";
    private static String docker_compose = "/usr/bin/docker-compose";
    private static String dockerComposeFile = "docker-compose.yml";
    private static String extensionDir = workDir + "/extensions/";

    public static void startService(String moduleName) throws Exception{
        checkFileExist(extensionDir + moduleName + File.separator,  dockerComposeFile);
        List<String> moduleNameList = new ArrayList<>();
        filterModuleInDockerCompose(extensionDir + moduleName + File.separator + dockerComposeFile, moduleNameList, new ArrayList<>());
        startService(new ArrayList<>(), new StringBuilder(), moduleNameList, extensionDir + moduleName + File.separator);
    }

    public static void stopService(String moduleName)throws Exception{
        checkFileExist(extensionDir + moduleName + File.separator,  dockerComposeFile);
        List<String> moduleNameList = new ArrayList<>();
        filterModuleInDockerCompose(extensionDir + moduleName + File.separator + dockerComposeFile, moduleNameList, new ArrayList<>());
        stopService(new ArrayList<>(), new StringBuilder(), extensionDir + moduleName + File.separator, moduleNameList);
    }

    public static void deleteService(String moduleName)throws Exception{
        checkFileExist(extensionDir + moduleName + File.separator,  dockerComposeFile);
        List<String> moduleNameList = new ArrayList<>();
        filterModuleInDockerCompose(extensionDir + moduleName + File.separator + dockerComposeFile, moduleNameList, new ArrayList<>());
        stopService(new ArrayList<>(), new StringBuilder(), extensionDir + moduleName + File.separator, moduleNameList);
        List<String> command = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        command.add("rm");
        command.add("-rf");
        command.add(extensionDir + moduleName);
        execCommand(result, command);
    }

    public static void installOrUpdateModule(String moduleName, String moduleFileName, boolean onLine) throws Exception {
        List<String> command = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        String random_dir_name = UUID.randomUUID().toString();
        String tmp_dir="/tmp/f2c-extensions/" + random_dir_name;
        String extensionTmpDir = tmp_dir + "/extension/";
        String extentionTmpConfFolder=extensionTmpDir + "conf";

        try{
            uncompress(moduleFileName, tmp_dir);
        }catch (Exception e) {
            LogUtil.error("Filed to uncompress module file" + e.getMessage());
            throw new Exception(e.getMessage());
        }

        checkFileExist(extensionTmpDir,  dockerComposeFile);

        if(!onLine){
            // 离线环境，需要事先把docker images 导入，这里不做处理
//            handleWithInternalDockerRegistry(extensionTmpDir);
        }

        List<String> newModuleNameList = new ArrayList<>();
        List<String> newImageNameList = new ArrayList<>();
        filterModuleInDockerCompose(extensionTmpDir + dockerComposeFile, newModuleNameList, newImageNameList);

        String fit2cloudModuleDir = extensionDir + moduleName + File.separatorChar ;
        String action = null;
        File moduleDir = new File(extensionDir + moduleName);
        if (moduleDir.exists()) {
            LogUtil.info(extensionDir+moduleName + "is exist," + " action=" + moduleActionUpdate);
            action = moduleActionUpdate;
        }else {
            LogUtil.info(extensionDir+moduleName + "is not exist," + " action=" + moduleActionInstall);
            action = moduleActionInstall;
            moduleDir.mkdirs();
        }

        if(action.equalsIgnoreCase("install")){
            File extentionTmpInstallScripts = new File(extensionTmpDir + "scripts/install.sh");
            if(extentionTmpInstallScripts.exists() && extentionTmpInstallScripts.isFile()){
                execScripts(command, result, extensionTmpDir + "scripts/install.sh");
            }
        }else {
            List<String> oldModuleNameList = new ArrayList<>();
            List<String> oldImageNameList = new ArrayList<>();
            filterModuleInDockerCompose(fit2cloudModuleDir + dockerComposeFile, oldModuleNameList, oldImageNameList);

            if(CollectionUtils.isNotEmpty(oldModuleNameList)){
                stopService(command, result, fit2cloudModuleDir, oldModuleNameList);
            }

            copyFile(command, result, fit2cloudModuleDir + dockerComposeFile, fit2cloudModuleDir + dockerComposeFile  + "_bak");

            File extentionTmpInstallScripts = new File(extensionTmpDir + "scripts/upgrade.sh");
            if(extentionTmpInstallScripts.exists() && extentionTmpInstallScripts.isFile()){
                execScripts(command, result, extensionTmpDir + "scripts/upgrade.sh");
            }

            for (String oldImage : oldImageNameList) {
                if(!newImageNameList.contains(oldImage)){
                    command.add(docker);
                    command.add("rmi");
                    command.add(oldImage);
                    execCommand(result, command);
                    result.setLength(0);
                    command.clear();
                }
            }
        }

        copyFile(command, result, extensionTmpDir + dockerComposeFile, fit2cloudModuleDir + dockerComposeFile);

        File extentionTmpConfFolderFile = new File(extentionTmpConfFolder);
        if(extentionTmpConfFolderFile.exists()){
            copyFile(command, result, extentionTmpConfFolder, fit2cloudModuleDir + "conf/");
        }

        try {
            pullImages(command, result, newImageNameList, onLine);
        }catch (Exception e ){
            LogUtil.error("Failed to pull images, ", e);
            F2CException.throwException(e);
        }

//        安装时，不启动
//        startService(command, result, newModuleNameList, fit2cloudModuleDir);

        deleteFile(command, result, tmp_dir);
    }

    private static void handleWithInternalDockerRegistry(String extensionTmpDir) throws IOException {
        DockerRegistry dockerRegistry = CommonBeanFactory.getBean(DockerRegistry.class);
        String url = dockerRegistry.getUrl().contains("://") ? dockerRegistry.getUrl().split("://")[1] : dockerRegistry.getUrl();
        if(url.endsWith("/")) url = url.substring(0, url.length() -1);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader newDockerComposebufferedReader = new BufferedReader(new FileReader(extensionTmpDir + dockerComposeFile));
        String line = null;
        while ((line=newDockerComposebufferedReader.readLine()) != null){
            if(line.contains("image:")){
                String image = line.replace("image:", "").replace(" ", "");
                line = line.replace(image.split("/")[0], url);
            }
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        newDockerComposebufferedReader.close();
        BufferedWriter newDockerComposebufferedWriter = new BufferedWriter(new FileWriter(extensionTmpDir + dockerComposeFile));
        newDockerComposebufferedWriter.write(stringBuilder.toString());
        newDockerComposebufferedWriter.flush();
        newDockerComposebufferedWriter.close();
    }

    private static void deleteFile(List<String> command, StringBuilder result, String tmp_dir) throws Exception {
        command.add("/bin/sh");
        command.add("rm" );
        command.add( "-rf " );
        command.add(tmp_dir);
        execCommand(result, command );
        result.setLength(0);
        command.clear();
    }

    private static void startService(List<String> command, StringBuilder result, List<String> moduleNameList, String fit2cloudModuleDir) throws Exception {
        LogUtil.info("Begin start application " +  moduleNameList);
        command.add(docker_compose);
        command.add("-f");
        command.add(workDir + File.separatorChar + dockerComposeFile);
        command.add("-f");
        command.add(fit2cloudModuleDir + File.separatorChar + dockerComposeFile);
        command.add("up");
        command.add("--no-recreate");
        command.add("-d");
        command.addAll(moduleNameList);
        int starCode = execCommand(result, command);
        if(starCode != 0){
            LogUtil.error("Start application failed: " + result.toString());
            throw new Exception("Start application failed: " + result.toString());
        }else {
            LogUtil.info("Success to Start application: " + moduleNameList, result.toString());
        }
        result.setLength(0);
    }

    private static void stopService(List<String> command, StringBuilder result, String fit2cloudModuleDir, List<String> moduleNameList) throws Exception {
        LogUtil.info("Begin stop application " +  moduleNameList);
        command.add(docker_compose);
        command.add( "-f");
        command.add(workDir + File.separator + dockerComposeFile);
        command.add("-f");
        command.add(fit2cloudModuleDir + File.separator +dockerComposeFile);
        command.add("rm");
        command.add("-sf");
        command.addAll(moduleNameList);
        int stopExitCode = execCommand(result, command);
        if(stopExitCode != 0){
            LogUtil.error("Stop application failed: " + result.toString());
            throw new Exception("Stop application failed: " + result.toString());
        }else {
            LogUtil.info("Success to stop application: " + result.toString());
        }
        result.setLength(0);
    }

    private static void pullImages(List<String> command, StringBuilder result, List<String> newImageNameList, boolean onLine) throws Exception {
//        离线环境，需要预先把docker images 导入，这里不做处理
//        if(!onLine){
//            DockerRegistry dockerRegistry = CommonBeanFactory.getBean(DockerRegistry.class);
//            command.add(docker);
//            command.add("login");
//            command.add(dockerRegistry.getUrl());
//            command.add("-u");
//            command.add(dockerRegistry.getUser());
//            command.add("-p");
//            command.add(dockerRegistry.getPasswd());
//            execCommand(result, command);
//            command.clear();
//            result.setLength(0);
//        }
        if(onLine){
            LogUtil.info("Pull images" +  newImageNameList);
            for (String image : newImageNameList) {
                command.add(docker);
                command.add("pull");
                command.add(image);
                int pullExitCode = execCommand(result, command);
                command.clear();
                if(pullExitCode != 0){
                    throw new Exception("Filed to pull image, " + image);
                }
            }
            LogUtil.info("Result of pull images: " + result.toString());
            result.setLength(0);
        }
    }

    private static void execScripts(List<String> command, StringBuilder result, String scipt) throws Exception {
        LogUtil.info("Exec " + scipt);
        command.add("/bin/sh");
        command.add(scipt);
        int exitCode = execCommand(result, command);
        command.clear();
        if(exitCode != 0){
            LogUtil.error("Result of exec {}: {}",  scipt, result.toString());
            throw new Exception("Filed to exec " + scipt + ", " + result.toString());
        }else {
            LogUtil.info("Result of exec {}: {}",  scipt, result.toString());
        }
        result.setLength(0);
    }

    private static void copyFile(List<String> command, StringBuilder result, String src, String dest) throws Exception {
        command.add("rm");
        command.add("-rf");
        execCommand(result, command);
        result.setLength(0);
        command.clear();
        LogUtil.info("Copy file from {} to {}", src, dest);
        command.add("cp");
        command.add("-r");
        command.add(src);
        command.add(dest);
        execCommand(result, command);
        command.clear();
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
        File extensionTmpDockerCompose = new File(path + fileName);
        if(!extensionTmpDockerCompose.exists() || !extensionTmpDockerCompose.isFile()){
            LogUtil.error("Cannot find {} in {}." , fileName, path);
            throw new Exception("Cannot find " + path + fileName );
        }
    }

    private static String filterModuleInDockerCompose(String dockerComposePath, List<String> moduleNameList, List<String> imageNameList) throws Exception{
        BufferedReader newDockerComposebufferedReader = new BufferedReader(new FileReader(dockerComposePath));
        String line = null;
        String moduleName = "";
        while ((line=newDockerComposebufferedReader.readLine()) != null){
            if(line.contains("image:")){
                imageNameList.add(line.replace("image:", "").replace(" ", ""));
            }
            if(line.contains("container_name:")){
                moduleNameList.add(line.replace("container_name:", "").replace(" ", ""));
                if(StringUtils.isEmpty(moduleName)){
                    moduleName = line.replace("container_name:", "").replace(" ", "");
                }
            }
        }

        if(StringUtils.isEmpty(moduleName) || CollectionUtils.isEmpty(moduleNameList) || CollectionUtils.isEmpty(imageNameList)){
            LogUtil.error("Cannot identify service from docker-compose.yml");
        }
        return moduleName;
    }

    private static int execCommand(StringBuilder result, List<String> command) throws Exception{
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        builder.directory(new File(workDir));
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

}
