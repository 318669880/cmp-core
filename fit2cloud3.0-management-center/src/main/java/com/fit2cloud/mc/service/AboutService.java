package com.fit2cloud.mc.service;


import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.license.DefaultLicenseService;
import com.fit2cloud.commons.server.license.F2CLicenseResponse;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

@Service
public class AboutService {

    private static final String BUILD_VERSION = "/opt/fit2cloud/conf/version";
    private static final String product = "cmp";

    @Resource
    private DefaultLicenseService defaultLicenseService;

    public F2CLicenseResponse updateLicense(String licenseKey) {
        F2CLicenseResponse f2CLicenseResponse = defaultLicenseService.updateLicense(product, licenseKey);
        OperationLogService.log(null, "license", "license", "LICENSE", ResourceOperation.UPDATE, null);
        return f2CLicenseResponse;
    }

    public F2CLicenseResponse validateLicense(String licenseKey) {
        if (StringUtils.isNotBlank(licenseKey)) {
            return defaultLicenseService.validateLicense(product, licenseKey);
        } else {
            return defaultLicenseService.validateLicense();
        }
    }

    public String getBuildVersion() {
        try {
            File file = new File(BUILD_VERSION);
            if (file.exists()) {
                String version = FileUtils.readFileToString(file, "UTF-8");
                if (StringUtils.isNotBlank(version)) {
                    return version;
                }
            }
            return GlobalConfigurations.getProperty("cmp.version", String.class, "V2.0");
        } catch (Exception e) {
            LogUtil.error("failed to get build version.", e);
        }
        return "unknown";
    }
}
