package com.fit2cloud.mc.utils;

import com.fit2cloud.commons.utils.LogUtil;
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
    private static String dockerCompose = "docker-compose.yml";
    private static String extensionDir = workDir + "/extensions/";

    public static void startService(String serviceName) throws Exception{
        checkFileExist(extensionDir + serviceName + File.separator,  dockerCompose);
        List<String> moduleNameList = new ArrayList<>();
        filterModuleInDockerCompose(extensionDir + serviceName + File.separator + dockerCompose, moduleNameList, new ArrayList<>());
        startService(new ArrayList<>(), new StringBuilder(), moduleNameList, extensionDir + serviceName + File.separator);
    }

    public static void stopService(String serviceName)throws Exception{
        checkFileExist(extensionDir + serviceName + File.separator,  dockerCompose);
        List<String> moduleNameList = new ArrayList<>();
        filterModuleInDockerCompose(extensionDir + serviceName + File.separator + dockerCompose, moduleNameList, new ArrayList<>());
        stopService(new ArrayList<>(), new StringBuilder(), extensionDir + serviceName + File.separator, moduleNameList);
    }


    public static void installOrUpdateModule(String moduleFileName) throws Exception {
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

        checkFileExist(extensionTmpDir,  dockerCompose);

        List<String> newModuleNameList = new ArrayList<>();
        List<String> newImageNameList = new ArrayList<>();
        String moduleName = filterModuleInDockerCompose(extensionTmpDir + dockerCompose, newModuleNameList, newImageNameList);

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
            filterModuleInDockerCompose(fit2cloudModuleDir + dockerCompose, oldModuleNameList, oldImageNameList);

            if(CollectionUtils.isNotEmpty(oldModuleNameList)){
                stopService(command, result, fit2cloudModuleDir, oldModuleNameList);
            }

            copyFile(command, result, fit2cloudModuleDir + dockerCompose, fit2cloudModuleDir + dockerCompose  + "_bak");

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

        copyFile(command, result, extensionTmpDir + dockerCompose, fit2cloudModuleDir + dockerCompose);

        File extentionTmpConfFolderFile = new File(extentionTmpConfFolder);
        if(extentionTmpConfFolderFile.exists()){
            copyFile(command, result, extentionTmpConfFolder, fit2cloudModuleDir + "conf/");
        }

        pullImages(command, result, newImageNameList);

        startService(command, result, newModuleNameList, fit2cloudModuleDir);

        deleteFile(command, result, tmp_dir);
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

    private static void startService(List<String> command, StringBuilder result, List<String> newModuleNameList, String fit2cloudModuleDir) throws Exception {
        LogUtil.info("Begin start new application " +  newModuleNameList);
        command.add(docker_compose);
        command.add("-f");
        command.add(workDir + File.separatorChar + dockerCompose);
        command.add("-f");
        command.add(fit2cloudModuleDir + File.separatorChar + dockerCompose);
        command.add("up");
        command.add("--no-recreate");
        command.add("-d");
        command.addAll(newModuleNameList);
        int starCode = execCommand(result, command);
        if(starCode != 0){
            LogUtil.error("Start new application failed: " + result.toString());
            throw new Exception("Start new application failed: " + result.toString());
        }else {
            LogUtil.info("Success to Start new application: " + result.toString());
        }
        result.setLength(0);
    }

    private static void stopService(List<String> command, StringBuilder result, String fit2cloudModuleDir, List<String> moduleNameList) throws Exception {
        LogUtil.info("Begin stop old application " +  moduleNameList);
        command.add(docker_compose);
        command.add( "-f");
        command.add(workDir + File.separator + dockerCompose);
        command.add("-f");
        command.add(fit2cloudModuleDir + File.separator +dockerCompose);
        command.add("rm");
        command.add("-sf");
        command.addAll(moduleNameList);
        int stopExitCode = execCommand(result, command);
        if(stopExitCode != 0){
            LogUtil.error("Stop old application failed: " + result.toString());
            throw new Exception("Stop old application failed: " + result.toString());
        }else {
            LogUtil.info("Success to stop old application: " + result.toString());
        }
        result.setLength(0);
    }

    private static void pullImages(List<String> command, StringBuilder result, List<String> newImageNameList) throws Exception {
        LogUtil.info("Pull images" +  newImageNameList);
        command.add(docker);
        command.add("pull");
        command.addAll(newImageNameList);
        int pullExitCode = execCommand(result, command);
        command.clear();
        if(pullExitCode != 0){
            LogUtil.error("Result of pull images: " + result.toString());
            throw new Exception("Filed to pull images, " + result.toString());
        }else {
            LogUtil.info("Result of pull images: " + result.toString());
        }
        result.setLength(0);
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

    private static void copyFile(List<String> command, StringBuilder result, String extentionTmpConfFolder, String fit2cloudModuleDirConf) throws Exception {
        LogUtil.info("Copy file from {} to {}", extentionTmpConfFolder, fit2cloudModuleDirConf);
        command.add("cp");
        command.add("-r");
        command.add(extentionTmpConfFolder);
        command.add(fit2cloudModuleDirConf);
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
        return exitCode;
    }

}
