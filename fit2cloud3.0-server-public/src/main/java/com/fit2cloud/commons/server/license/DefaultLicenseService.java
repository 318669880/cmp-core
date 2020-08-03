package com.fit2cloud.commons.server.license;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.License;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.utils.CodingUtil;
import com.fit2cloud.commons.utils.DateUtil;
import com.fit2cloud.license.core.constants.LicenseConstants;
import com.fit2cloud.license.core.exception.LicenseException;
import com.fit2cloud.license.core.exception.LicenseExceptionCode;
import com.fit2cloud.license.core.model.F2CLicense;
import com.fit2cloud.license.core.model.F2CLicenseExpand;
import com.fit2cloud.license.core.model.F2CLicenseResponse;
import com.fit2cloud.license.core.service.BasicLicenseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefaultLicenseService {
    @Resource
    private BasicLicenseService basicLicenseService;
    @Resource
    private InnerLicenseService innerLicenseService;
    private static final String LICENSE_ID = "fit2cloud_license";
    private static final String EXPIRED_FORMAT = LicenseConstants.EXPIRED_FORMAT;
    private static final String EXPIRED_FORMAT_PATTERN = LicenseConstants.EXPIRED_FORMAT_PATTERN;
    private static final String VERSION = "01";


    public F2CLicenseResponse validateLicense() {
        try {
            String licenseKey = readLicense();
            return basicLicenseService.validateLicenseKey(LicenseConstants.Product.CMP, licenseKey);
        } catch (Exception e) {
            return F2CLicenseResponse.fail(e.getMessage());
        }
    }

    public F2CLicenseResponse updateLicense(LicenseConstants.Product product, String licenseKey) {
        // 验证license
        F2CLicenseResponse response = basicLicenseService.validateLicenseKey(product, licenseKey);
        if (response.getStatus() == LicenseConstants.Status.Fail) {
            return response;
        }
        // 覆盖原license
        writeLicense(licenseKey);
        return response;
    }


    public void createLicense() {
        // 只有License记录不存或者内容为空时创建Demo License
        if (!innerLicenseService.existLicense(LICENSE_ID)) {
            generateDemoLicense();
        }
    }

    // 生成Demo License的license key记录
    private void generateDemoLicense() {
        Date expired = DateUtil.addDays(new Date(), LicenseConstants.DEMO_EXPIRED_DAY);
        F2CLicense license = new F2CLicense(LicenseConstants.DEMO_CORPORATION,
                DateUtil.getDate2String(EXPIRED_FORMAT, expired),
                LicenseConstants.Edition.Standard,
                LicenseConstants.Product.CMP,
                LicenseConstants.DEMO_COUNT);
        String licenseKey = basicLicenseService.generateLicenseKey(license);
        writeLicense(licenseKey);
    }

    // BASE64字符串转换为F2CLicense
    private F2CLicense convertLicense(String base64) {
        String json = CodingUtil.base64Decoding(base64);
        F2CLicenseExpand expand = JSONObject.parseObject(json, F2CLicenseExpand.class);
        return expand.getLicense();
    }

    // F2CLicense转换为BASE64字符串
    private String convertString(F2CLicense license) {
        String name = license.getCorporation();
        if (StringUtils.isBlank(name)) {
            throw new LicenseException(Translator.get("i18n_ex_corporation_empty"));
        }
        String expired = license.getExpired();
        Pattern p = Pattern.compile(EXPIRED_FORMAT_PATTERN);
        Matcher m = p.matcher(expired);
        if (!m.matches()) {
            throw new LicenseException(Translator.get("i18n_ex_expire_date_invalid"));
        }
        try {
            Date date = DateUtil.truncate(DateUtil.parseDate(expired, EXPIRED_FORMAT), Calendar.DAY_OF_MONTH);
            if (date.before(new Date())) {
                throw new LicenseException(Translator.get("i18n_ex_expire_date_must_greater_than_current"));
            }
        } catch (ParseException e) {
            throw new LicenseException(Translator.get("i18n_ex_expire_date_incorrect"));
        }

        F2CLicenseExpand expand = new F2CLicenseExpand();
        expand.setCreate(DateUtil.getCurrentFormatDate(DateUtil.YYYY_MM_DD_HH_MM_SS));
        expand.setVersion(VERSION);
        expand.setLicense(license);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            builder.append(UUID.randomUUID().toString());
        }
        expand.setRandom(builder.toString());

        return CodingUtil.base64Encoding(JSONObject.toJSONString(expand));
    }

    // 从数据库读取License
    private String readLicense() {
        License license = innerLicenseService.getLicense(LICENSE_ID);
        if (license == null) {
            throw new LicenseException(LicenseExceptionCode.NoLicense);
        }
        if (StringUtils.isBlank(license.getLicense())) {
            throw new LicenseException(LicenseExceptionCode.EmptyLicense);
        }
        return license.getLicense();
    }

    // 创建或更新License
    private void writeLicense(String licenseKey) {
        if (StringUtils.isBlank(licenseKey)) {
            throw new LicenseException(LicenseExceptionCode.EmptyKey);
        }
        License license = new License();
        license.setId(LICENSE_ID);
        license.setLicense(licenseKey);
        innerLicenseService.saveLicense(license);
    }
}
