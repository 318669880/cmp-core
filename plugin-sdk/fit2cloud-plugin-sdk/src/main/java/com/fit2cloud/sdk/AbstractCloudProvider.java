package com.fit2cloud.sdk;

import com.fit2cloud.sdk.constants.F2CPricePolicy;
import com.fit2cloud.sdk.constants.F2CResourceType;
import com.fit2cloud.sdk.constants.InitMethod;
import com.fit2cloud.sdk.constants.Language;
import com.fit2cloud.sdk.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Created by zhangbohan on 15/7/29.
 */
public abstract class AbstractCloudProvider implements ICloudProvider {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, Map<String, String>> i18nManager = new HashMap<>();

    public AbstractCloudProvider() {
        try {
            // 读取各插件里的国际化文件
            for (Language language : Language.values()) {
                Type mapType = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> result = new Gson().fromJson(readConfigFile("i18n/" + language + ".json"), mapType);

                i18nManager.put(language.name(), result);
            }
            // 初始化
            log.info("load i18n success, plugin: " + getName());
        } catch (Exception e) {
            log.error("load i18n failed. plugin: " + getName());
        }
    }

    public Map<String, Map<String, String>> getI18nMap() {
        return i18nManager;
    }

    public String getPageTemplate() throws PluginException {
        return getPageTemplate(F2CResourceType.VM);
    }

    public String getPageTemplate(String resourceType) throws PluginException {
        String pageFile = "launchConfigure.json";
        if (F2CResourceType.VM.equalsIgnoreCase(resourceType)) {
            pageFile = "launchConfigure.json";
        } else if (F2CResourceType.IP.equalsIgnoreCase(resourceType)) {
            pageFile = "ip.json";
        } else if (F2CResourceType.DISK.equalsIgnoreCase(resourceType)) {
            pageFile = "diskConfigure.json";
        } else if (F2CResourceType.IMAGE.equalsIgnoreCase(resourceType)) {
            pageFile = "imageConfigure.json";
        } else {

            GetPageTemplateRequest request = new Gson().fromJson(resourceType, GetPageTemplateRequest.class);
            String requestResourceType = request.getResourceType();
            if (StringUtils.isEmpty(requestResourceType)) {
                // 默认查询 vm
                requestResourceType = F2CResourceType.VM;
            }
            return getPageTemplate(requestResourceType);
        }
        return readConfigFile(pageFile);
    }


    public String getCredentialPageTemplate() throws PluginException {
        return readConfigFile("credential.json");
    }


    public String getPluginInfo() throws PluginException {
        return readConfigFile("pluginInfo.json");
    }

    @SuppressWarnings("resource")
    private String readConfigFile(String fileName) throws PluginException {
        InputStream is = null;
        BufferedReader br = null;
        try {
            URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
            JarFile jarFile = new JarFile(url.getPath());
            is = jarFile.getInputStream(jarFile.getEntry(fileName));
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Failed to load json file. ");
            throw new PluginException("The plugin does not yet support this operation.!");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
                is = null;
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
                br = null;
            }
        }
    }

    public F2CTemplateInfo getF2CTemplateInfo() throws PluginException {
        return null;
    }

    public boolean isSupportGetMetricByHost() {
        return false;
    }

    public boolean isSupportIndependentDiskManagement() {
        return false;
    }

    public List<F2CLoadBalancer> getF2CLoadBalancers(String getLoadBalancersRequest) throws PluginException {
        return null;
    }

    public List<F2CEntityPerfMetric> getF2CPerfMetricList(GetMetricsRequest perfMetricsRequest) throws PluginException {
        return null;
    }

    public List<F2CEvent> getF2CEventList(GetEventsRequest eventsRequest) throws PluginException {
        return null;
    }

    public List<F2CAlarm> getF2CAlarmList(Request request) throws PluginException {
        return null;
    }

    @Override
    public List<F2CImage> getF2CImagesWithParam(RequestWithParam getImagesRequest) throws PluginException {
        return null;
    }

    public void editDisk(String request) throws PluginException {
        throw new PluginException("The plugin does not yet support this operation.!");
    }

    public List<F2CDisk> getF2CDiskList(Request disksRequest) throws PluginException {
        return null;
    }

    public F2CDiskMetric getF2CDiskMetric(GetDiskMetricRequest metricsRequest) throws PluginException {
        return null;
    }

    public List<F2CDatastoreMetric> getF2CDatastoreMetrics(GetMetricsRequest datastoreMetricRequest) throws PluginException {
        return null;
    }

    @Override
    public List<InitMethod> getSupportedInitMethod(String request) throws PluginException {
        return null;
    }

    public String getF2CPricePolicy() {
        return F2CPricePolicy.INSTANCE_TYPE;
    }

    public String getPlatformVersion(Request request) throws PluginException {
        return null;
    }

    public List<F2CKeyPair> getIpPolicies(String obj) {
        List<F2CKeyPair> list = new ArrayList<F2CKeyPair>();
        list.add(new F2CKeyPair("DHCP(Auto)", "dhcp"));
        return list;
    }

    public String createResource(String createResourceRequest) throws PluginException {
        CreateResourceRequest req;
        try {
            req = new Gson().fromJson(createResourceRequest, CreateResourceRequest.class);
        } catch (JsonSyntaxException e) {
            throw new PluginException("Please check the request parameters!");
        }
        String resType = req.getResourceType();
        if (resType == null || resType.trim().length() == 0) {
            throw new PluginException("Please check the request parameters!");
        }
        if (F2CResourceType.VM.equalsIgnoreCase(resType)) {
            F2CInstance res = launchInstance(createResourceRequest);
            if (res != null) {
                return new Gson().toJson(res);
            }
            return null;
        } else if (F2CResourceType.DISK.equalsIgnoreCase(resType)) {
            F2CDisk res = createDisk(createResourceRequest);
            if (res != null) {
                return new Gson().toJson(res);
            }
            return null;
        } else if (F2CResourceType.IMAGE.equalsIgnoreCase(resType)) {
            F2CImage res = createImage(createResourceRequest);
            if (res != null) {
                return new Gson().toJson(res);
            }
            return null;
        }
        throw new PluginException("The plugin does not yet support the creation of this type of resource.");
    }

    public void executeScript(String executeScriptRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support script execution.");
    }

    public void resetPassword(String resetPasswordRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support resetting the login password!");
    }

    public void allocateIp(String allocateIpRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support assigning IP");
    }

    public F2CDisk createDisk(String createDiskRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support creating disks!");
    }

    public List<F2CDisk> createVolume(String request) throws PluginException {
        throw new PluginException("The plugin does not yet support creating volumes!");
    }

    public boolean updateVolume(String request) throws PluginException {
        throw new PluginException("The plugin does not yet support updating volume!");
    }

    public boolean enlargeVolume(String request) throws PluginException {
        throw new PluginException("The plugin does not yet support enlarge volume!");
    }

    public F2CDisk attachVolumeToInstance(String request) throws PluginException {
        throw new PluginException("The plugin does not yet support attach volume!");
    }

    public boolean detachVolumeFromInstance(String request) throws PluginException {
        throw new PluginException("The plugin does not yet support detach volume!");
    }

    public boolean deleteDisk(String releaseDiskRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support deleting disk resources!");
    }

    public F2CImage createImage(String createImageRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support creating images!");
    }

    public boolean checkDiskSize(String request) throws PluginException {
        return true;
    }

    public boolean checkDiskName(String request) throws PluginException {
        return true;
    }

    public void resetHostname(String resetHostnameRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support resetting hostname!");
    }

    public void addDomain(String addDomainRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support add domain!");
    }

    public boolean releaseResource(String releaseResourceRequest) throws PluginException {
        ReleaseResourceRequest req;
        try {
            req = new Gson().fromJson(releaseResourceRequest, ReleaseResourceRequest.class);
        } catch (JsonSyntaxException e) {
            throw new PluginException("Please check the request parameters!");
        }
        String resType = req.getResourceType();
        if (resType == null || resType.trim().length() == 0) {
            throw new PluginException("Please check the resource type!");
        }
        String resId = req.getResourceId();
        if (resId == null || resId.trim().length() == 0) {
            throw new PluginException("Please check the resource ID!");
        }
        if (F2CResourceType.VM.equalsIgnoreCase(resType)) {
            TerminateInstanceRequest terminateInstanceReq = new Gson().fromJson(releaseResourceRequest, TerminateInstanceRequest.class);
            terminateInstanceReq.setInstanceId(req.getResourceId());
            return terminateInstance(terminateInstanceReq);
        } else if (F2CResourceType.DISK.equalsIgnoreCase(resType)) {
            return deleteDisk(releaseResourceRequest);
        } else if (F2CResourceType.IMAGE.equalsIgnoreCase(resType)) {
            return deleteImage(new Gson().fromJson(releaseResourceRequest, DeleteImageRequest.class));
        }
        throw new PluginException("The plugin does not yet support the creation of this type of resource.");
    }

    public String startResource(String startResourceRequest) throws PluginException {
        StartResourceRequest req;
        try {
            req = new Gson().fromJson(startResourceRequest, StartResourceRequest.class);
        } catch (JsonSyntaxException e) {
            throw new PluginException("Please check the request parameters!");
        }

        String resType = req.getResourceType();
        if (resType == null || resType.trim().length() == 0) {
            throw new PluginException("Please check the resource type!");
        }
        String resId = req.getResourceId();
        if (resId == null || resId.trim().length() == 0) {
            throw new PluginException("Please check the resource ID!");
        }
        if (F2CResourceType.VM.equalsIgnoreCase(resType)) {
            StartInstanceRequest startInstanceRequest = new Gson().fromJson(startResourceRequest, StartInstanceRequest.class);
            startInstanceRequest.setInstanceId(req.getResourceId());
            F2CInstance result = startInstance(startInstanceRequest);
            return result == null ? null : new Gson().toJson(result);
        } else {
            throw new PluginException("The plugin does not yet support the release of this type of resource.");
        }
    }

    public void stopResource(String stopResourceRequest) throws PluginException {
        StopResourceRequest req;
        try {
            req = new Gson().fromJson(stopResourceRequest, StopResourceRequest.class);
        } catch (JsonSyntaxException e) {
            throw new PluginException("Please check the request parameters!");
        }

        String resType = req.getResourceType();
        if (resType == null || resType.trim().length() == 0) {
            throw new PluginException("Please check the resource type!");
        }
        String resId = req.getResourceId();
        if (resId == null || resId.trim().length() == 0) {
            throw new PluginException("Please check the resource ID!");
        }
        if (F2CResourceType.VM.equalsIgnoreCase(resType)) {
            StopInstanceRequest stopInstanceRequest = new Gson().fromJson(stopResourceRequest, StopInstanceRequest.class);
            stopInstanceRequest.setInstanceId(req.getResourceId());
            stopInstance(stopInstanceRequest);
        } else {
            throw new PluginException("The plugin does not yet support the stop of this type of resource!");
        }
    }

    public void rebootResource(String rebootResourceRequest) throws PluginException {
        RebootResourceRequest req;
        try {
            req = new Gson().fromJson(rebootResourceRequest, RebootResourceRequest.class);
        } catch (JsonSyntaxException e) {
            throw new PluginException("Please check the request parameters!");
        }

        String resType = req.getResourceType();
        if (resType == null || resType.trim().length() == 0) {
            throw new PluginException("Please check the resource type!");
        }
        String resId = req.getResourceId();
        if (resId == null || resId.trim().length() == 0) {
            throw new PluginException("Please check the resource ID!");
        }
        if (F2CResourceType.VM.equalsIgnoreCase(resType)) {
            RebootOSRequest RebootOSRequest = new Gson().fromJson(rebootResourceRequest, RebootOSRequest.class);
            RebootOSRequest.setInstanceId(req.getResourceId());
            rebootInstance(RebootOSRequest);
        } else {
            throw new PluginException("The plugin does not yet support the stop of this type of resource!");
        }

    }

    public F2CInstance allocateResource(String allocateResourceRequest, String type) throws PluginException {
        throw new PluginException("The plugin does not yet support tuning resource configuration!");
    }

    public List<F2CDisk> getVmF2CDisks(String vmDisksRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support getting virtual machine disks!");
    }

    public List<F2CSnapshot> getVmF2CSnapshots(String snapshotRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support taking snapshots!");
    }

    public boolean createSnapshot(String snapshotRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support creating snapshots!");
    }

    public boolean revertToSnapshot(String snapshotRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support recovery snapshots!");
    }

    public boolean deleteSnapshot(String snapshotRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support deleting snapshots!");
    }

    public String getRemoteConsoleUrl(String snapshotRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support Remote Desktop!");
    }

    public boolean shutdownInstance(ShutdownOSRequest shutdownOSRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support shutting down the guest operating system!");
    }

    public boolean rebootInstance(RebootOSRequest rebootOSRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support restarting the guest operating system!");
    }

    public F2CInstanceSize getInstanceSizeInfo(String launchInstanceRequest) throws PluginException {
        return new F2CInstanceSize();
    }

    public F2CCapacity getCapacity(String launchInstanceRequest) throws PluginException {
        throw null;
    }

    public boolean isSupportSetThreshold() {
        return false;
    }

    public String cloneInstance(String cloneResourceRequest) throws PluginException {
        throw new PluginException("The plugin does not yet support clone the guest operating system!");
    }

    public boolean isSupportCloneInstance() {
        return false;
    }

    public boolean isSupportSnapshotManage() {
        return false;
    }

    public boolean isSupportgetRemoteConsoleUrl() {
        return false;
    }

    public boolean isSupportMultiNetwork() {
        return false;
    }

    public List<F2CNetwork> getF2cNetworks(Request networkRequest) throws PluginException{
        return new ArrayList<F2CNetwork>();
    };

    public <T> T invokeCustomMethod(String methodName, Object... parameters) throws PluginException {
        try {
            List<Class> paramsClass = new ArrayList<Class>();
            for (Object param : parameters) {
                paramsClass.add(param.getClass());
            }
            Method targetMethod = this.getClass().getDeclaredMethod(methodName, paramsClass.toArray(new Class[]{}));
            return (T) targetMethod.invoke(this, parameters);
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }
}
