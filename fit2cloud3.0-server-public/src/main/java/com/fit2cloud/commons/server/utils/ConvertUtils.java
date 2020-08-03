package com.fit2cloud.commons.server.utils;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.constants.CloudAlarmStatus;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.sdk.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ConvertUtils {
    private static Pattern SEC_TIMESTAMP = Pattern.compile("^[0-9]{10}$");

    private static <T, R> R convert(Function<T, R> function, T t) {
        return function.apply(t);
    }

    public static CloudImage image(F2CImage f2CImage) {
        return convert((image) -> {
            CloudImage cloudImage = new CloudImage();
            try {
                BeanUtils.copyBean(cloudImage, image);
            } catch (Exception e) {
                LogUtil.error("image copy bean error", e);
            }
            cloudImage.setId(null);
            cloudImage.setImageName(image.getName());
            cloudImage.setImageId(image.getId());
            cloudImage.setCreateTime(Optional.ofNullable(f2CImage.getCreated()).map((date) -> {
                if (SEC_TIMESTAMP.matcher(date.toString()).matches()) {
                    return date * 1000;
                } else {
                    return date;
                }
            }).orElse(null));
            return cloudImage;
        }, f2CImage);
    }

    public static CloudServer server(F2CInstance f2CInstance) {
        return convert(server -> {
            CloudServer cloudServer = new CloudServer();
            try {
                BeanUtils.copyBean(cloudServer, f2CInstance);
            } catch (Exception e) {
                LogUtil.error("cloud_server 对象转换出错 {}", ExceptionUtils.getStackTrace(e));
            }
            cloudServer.setInstanceUuid(f2CInstance.getInstanceUUID());
            cloudServer.setInstanceName(f2CInstance.getName());
            cloudServer.setRemoteIp(f2CInstance.getRemoteIP());
            cloudServer.setOsInfo(f2CInstance.getOs());
            cloudServer.setOs(null);
            if (CollectionUtils.isNotEmpty(f2CInstance.getIpArray())) {
                cloudServer.setIpArray(JSON.toJSONString(f2CInstance.getIpArray()));
            }
            return cloudServer;
        }, f2CInstance);
    }

    public static CloudHost host(F2CHost f2CHost) {
        return convert(server -> {
            CloudHost cloudHost = new CloudHost();
            try {
                BeanUtils.copyBean(cloudHost, f2CHost);
                cloudHost.setRegion(f2CHost.getDataCenterId());
                cloudHost.setZone(f2CHost.getClusterId());
                cloudHost.setCpuAllocated(f2CHost.getCpuMHzAllocated());
                cloudHost.setCpuTotal(f2CHost.getCpuMHzTotal());
                cloudHost.setCpuMhzPerOneCore(f2CHost.getCpuMHzPerOneCore());
            } catch (Exception e) {
                LogUtil.error("cloud_host 对象转换出错 {}", ExceptionUtils.getStackTrace(e));
            }
            return cloudHost;
        }, f2CHost);
    }

    public static CloudDatastore datastore(F2CDataStore store) {
        return convert(server -> {
            CloudDatastore cloudDatastore = new CloudDatastore();
            try {
                BeanUtils.copyBean(cloudDatastore, store);
                cloudDatastore.setRegion(store.getDataCenterId());
                cloudDatastore.setZone(store.getClusterId());
                cloudDatastore.setDatastoreId(store.getDataStoreId());
                cloudDatastore.setDatastoreName(store.getDataStoreName());

            } catch (Exception e) {
                LogUtil.error("cloud_datastore 对象转换出错 {}", ExceptionUtils.getStackTrace(e));
            }
            return cloudDatastore;
        }, store);
    }

    public static CloudDisk disk(F2CDisk disk) {
        return convert(f2cDisk -> {
            CloudDisk cloudDisk = new CloudDisk();
            try {
                BeanUtils.copyBean(cloudDisk, disk);
                cloudDisk.setDatastoreId(disk.getDatastoreUniqueId());
            } catch (Exception e) {
                LogUtil.error("cloud_disk 对象转换出错 {}", ExceptionUtils.getStackTrace(e));
            }
            return cloudDisk;
        }, disk);
    }

    public static CloudEvent event(F2CEvent event) {
        return convert(f2cDisk -> {
            CloudEvent cloudEvent = new CloudEvent();
            try {
                BeanUtils.copyBean(cloudEvent, event);
                cloudEvent.setRegion(event.getDatacenterName());
                cloudEvent.setZone(event.getClusterName());
            } catch (Exception e) {
                LogUtil.error("cloud_event 对象转换出错 {}", ExceptionUtils.getStackTrace(e));
            }
            return cloudEvent;
        }, event);
    }

    public static CloudAlarm alarm(F2CAlarm alarm) {
        return convert(f2cDisk -> {
            CloudAlarm cloudAlarm = new CloudAlarm();
            try {
                BeanUtils.copyBean(cloudAlarm, alarm);
                cloudAlarm.setId(alarm.getAlarmId());
                cloudAlarm.setStatus(CloudAlarmStatus.active.name());
                cloudAlarm.setResourceType(alarm.getResourceType().name());
            } catch (Exception e) {
                LogUtil.error("cloud_alarm 对象转换出错 {}", ExceptionUtils.getStackTrace(e));
            }
            return cloudAlarm;
        }, alarm);
    }
}
