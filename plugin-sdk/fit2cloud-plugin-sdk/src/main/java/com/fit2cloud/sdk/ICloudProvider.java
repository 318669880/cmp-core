package com.fit2cloud.sdk;

import com.fit2cloud.sdk.constants.InitMethod;
import com.fit2cloud.sdk.model.*;

import java.util.List;

public interface ICloudProvider {
    /**返回插件名称**/
    String getName();

    /**获取插件支持的初始化方法**/
    List<InitMethod> getSupportedInitMethod(String request) throws PluginException;

    /**获取价格策略**/
    String getF2CPricePolicy();

    /**获取平台版本**/
    String getPlatformVersion(Request request) throws PluginException;

    /**验证云帐号是否有效**/
    boolean validateCredential(String credential) throws PluginException;

    /**创建并启动虚机实例**/
    F2CInstance launchInstance(String launchInstanceRequest) throws PluginException;

    /**验证启动虚机配置,如果不能通过验证抛出异常**/
    void validateLaunchInstanceConfiguration(String launchInstanceConfiguration) throws PluginException;

    /**删除虚机实例**/
    boolean terminateInstance(TerminateInstanceRequest terminateInstanceRequest) throws PluginException;

    /**启动虚机实例**/
    F2CInstance startInstance(StartInstanceRequest startInstanceRequest) throws PluginException;

    /**停止虚机实例**/
    boolean stopInstance(StopInstanceRequest stopInstanceRequest) throws PluginException;

    /**关闭客户机操作系统**/
    boolean shutdownInstance(ShutdownOSRequest shutdownOSRequest) throws PluginException;

    /**重启客户机操作系统**/
    boolean rebootInstance(RebootOSRequest rebootOSRequest) throws PluginException;

    /**获取全部实例列表**/
    List<F2CInstance> getF2CInstances(String getInstancesRequest) throws PluginException;

    /**获取单个实例的信息**/
    F2CInstance getF2CInstance(String getInstanceRequest) throws PluginException;

    /**获取全部负载均衡器列表**/
    List<F2CLoadBalancer> getF2CLoadBalancers(String getLoadBalancersRequest) throws PluginException;

    /**制作镜像**/
    F2CImage createImage(CreateImageRequest createImageRequest) throws PluginException;

    /**删除镜像**/
    boolean deleteImage(DeleteImageRequest deleteImageRequest) throws PluginException;

    /**获取全部自定义镜像列表**/
    List<F2CImage> getF2CImages(Request getImagesRequest) throws PluginException;

    /**获取筛选参数镜像列表**/
    List<F2CImage> getF2CImagesWithParam(RequestWithParam getImagesRequest) throws PluginException;

    /**获取host主机列表**/
    List<F2CHost> getF2CHosts(Request getHostsRequest) throws PluginException;

    /**获取datastore存储列表**/
    List<F2CDataStore> getF2CDataStores(Request getF2CDataStoresRequest) throws PluginException;

    /**获取插件模板描述信息**/
    F2CTemplateInfo getF2CTemplateInfo() throws PluginException;

    /**是否支持根据宿主机获取监控**/
    boolean isSupportGetMetricByHost();

    /**获取插件DataStore， Host， VM 的Performance metric**/
    List<F2CEntityPerfMetric> getF2CPerfMetricList(GetMetricsRequest perfMetricsRequest) throws PluginException;

    /**获取插件的事件**/
    List<F2CEvent> getF2CEventList(GetEventsRequest eventsRequest) throws PluginException;

    /**获取插件的告警**/
    List<F2CAlarm> getF2CAlarmList(Request request) throws PluginException;

    /**获取磁盘监控数据**/
    F2CDiskMetric getF2CDiskMetric(GetDiskMetricRequest metricsRequest) throws PluginException;

    /**获取Datastore相关数据**/
    List<F2CDatastoreMetric> getF2CDatastoreMetrics(GetMetricsRequest metricsRequest) throws PluginException;

    /**创建资源**/
    String createResource(String createResourceRequest) throws PluginException;

    /**在虚机上执行脚本**/
    void executeScript(String executeScriptRequest) throws PluginException;

    /**重置登录密码**/
    void resetPassword(String resetPasswordRequest) throws PluginException;

    /**重置hostname**/
    void resetHostname(String resetHostnameRequest) throws PluginException;

    /** windows 加域**/
    void addDomain(String addDomainRequest) throws PluginException;

    /**为指定虚机分配IP**/
    void allocateIp(String allocateIpRequest) throws PluginException;

    /** 磁盘编辑, 支持新增和扩容**/
    void editDisk(String request) throws PluginException;

    /**获取磁盘列表**/
    List<F2CDisk> getF2CDiskList(Request disksRequest) throws PluginException;

    /**创建磁盘资源**/
    F2CDisk createDisk(String createDiskRequest) throws PluginException;
    List<F2CDisk> createVolume(String request) throws PluginException;

    /**更新磁盘名称，描述**/
    boolean updateVolume(String request) throws PluginException;

    /**扩容磁盘**/
    boolean enlargeVolume(String request) throws PluginException;

    /**是否支独立磁盘管理功能：挂载、卸载、删除**/
    boolean isSupportIndependentDiskManagement();

    /**挂载磁盘**/
    F2CDisk attachVolumeToInstance(String request) throws PluginException;

    /**卸载磁盘**/
    boolean detachVolumeFromInstance(String request) throws PluginException;

    /**释放磁盘资源**/
    boolean deleteDisk(String releaseDiskRequest) throws PluginException;

    /**检测磁盘类型对应的容量是否满足**/
    boolean checkDiskSize(String request)throws PluginException;

    /**检测磁盘name是否满足**/
    boolean checkDiskName(String request)throws PluginException;

    /**创建镜像资源**/
    F2CImage createImage(String crateImageRequest) throws PluginException;

    /**释放资源**/
    boolean releaseResource(String releaseResourceRequest) throws PluginException;

    /**自服务启动资源**/
    String startResource(String startResourceRequest) throws PluginException;

    /**自服务停止资源**/
    void stopResource(String stopResourceRequest) throws PluginException;

    /**自服务重启操作系统**/
    void rebootResource(String rebootResourceRequest) throws PluginException;

    /** 调整资源配置**/
    F2CInstance allocateResource(String allocateResourceRequest, String type) throws PluginException;

    /**获取虚机上的磁盘列表**/
    List<F2CDisk> getVmF2CDisks(String vmDisksRequest) throws PluginException;

    /**获取虚机上的快照列表**/
    List<F2CSnapshot> getVmF2CSnapshots(String snapshotRequest) throws PluginException;

    /** 创建快照**/
    boolean createSnapshot(String snapshotRequest) throws PluginException;

    /** 恢复快照**/
    boolean revertToSnapshot(String snapshotRequest) throws PluginException;

    /** 删除快照**/
    boolean deleteSnapshot(String snapshotRequest) throws PluginException;

    /** 获取远程桌面**/
    String getRemoteConsoleUrl(String getRemoteConsoleRequest) throws PluginException;

    /** 获取实例类型信息**/
    F2CInstanceSize getInstanceSizeInfo(String launchInstanceRequest) throws PluginException;

    /**获取容量**/
    F2CCapacity getCapacity(String launchInstanceRequest) throws PluginException;

    boolean isSupportSetThreshold();

    /**克隆资源**/
    String cloneInstance(String cloneResourceRequest) throws PluginException;

    /**是否支独虚拟机克隆**/
    boolean isSupportCloneInstance();

    /**是否支持vnc登陆**/
    boolean isSupportgetRemoteConsoleUrl();

    /**是否支持快照管理**/
    boolean isSupportSnapshotManage();

    /**是否支持多网卡管理**/
    boolean isSupportMultiNetwork();

    /**获取网络**/
    List<F2CNetwork> getF2cNetworks(Request networkRequest) throws PluginException;

    /**获取子网可用IP数**/
    String subnetIpAvailabilityCount(String request);

    /**公有云虚拟机变更所属项目、vc虚拟机变更所属文件夹**/
    boolean migrateResource(String request);

    /** 自定义方法，不通用的逻辑请override这个方法**/
    <T> T invokeCustomMethod(String methodName, Object... parameters) throws PluginException;
}
