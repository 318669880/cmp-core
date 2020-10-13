package com.fit2cloud.commons.server.license;


import com.fit2cloud.commons.server.base.domain.License;
import com.fit2cloud.commons.server.base.mapper.LicenseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Transactional(rollbackFor = Exception.class)
class InnerLicenseService {

    @Resource
    private LicenseMapper licenseMapper;

    boolean existLicense(String key) {
        License license = licenseMapper.selectByPrimaryKey(key);
        return license != null;
    }

    License getLicense(String key) {
        License license = licenseMapper.selectByPrimaryKey(key);
        if (license == null) return null;
        return license;
    }

    void saveLicense(License license) {
        license.setUpdateTime(new Date());
        if (existLicense(license.getId())) {
            licenseMapper.updateByPrimaryKeyWithBLOBs(license);
        } else {
            licenseMapper.insert(license);
        }
    }


}
