package com.fit2cloud.commons.server.license;


import com.fit2cloud.commons.server.base.domain.License;
import com.fit2cloud.commons.server.base.mapper.LicenseMapper;
import com.fit2cloud.commons.utils.CodingUtil;
import com.fit2cloud.license.core.constants.LicenseConstants;
import com.fit2cloud.license.core.exception.LicenseException;
import com.fit2cloud.license.core.exception.LicenseExceptionCode;
import org.apache.commons.lang3.StringUtils;
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
        return decrypt(license);
    }

    void saveLicense(License license) {
        license.setUpdateTime(new Date());
        if (existLicense(license.getId())) {
            licenseMapper.updateByPrimaryKeyWithBLOBs(encrypt(license));
        } else {
            licenseMapper.insert(encrypt(license));
        }
    }

    private License decrypt(License license) {
        String licenseKey = license.getLicense();
        String decrypt;
        try {
            decrypt = CodingUtil.aesDecrypt(licenseKey, LicenseConstants.getSecretKey(), LicenseConstants.getIv());
        } catch (Exception e) {
            throw new LicenseException(LicenseExceptionCode.DecryptError);
        }
        if (StringUtils.equals(licenseKey, decrypt)) {
            throw new LicenseException(LicenseExceptionCode.DecryptError);
        }
        license.setLicense(decrypt);
        return license;
    }

    private License encrypt(License license) {
        String licenseKey = license.getLicense();
        String encrypt;
        try {
            encrypt = CodingUtil.aesEncrypt(licenseKey, LicenseConstants.getSecretKey(), LicenseConstants.getIv());
        } catch (Exception e) {
            throw new LicenseException(LicenseExceptionCode.EncryptError);
        }
        if (StringUtils.equals(licenseKey, encrypt)) {
            throw new LicenseException(LicenseExceptionCode.EncryptError);
        }
        license.setLicense(encrypt);
        return license;
    }
}
