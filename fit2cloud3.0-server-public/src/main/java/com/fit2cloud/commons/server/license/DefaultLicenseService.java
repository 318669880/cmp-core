package com.fit2cloud.commons.server.license;

import com.fit2cloud.commons.server.base.domain.License;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;


@Service
public class DefaultLicenseService {
    @Resource
    private InnerLicenseService innerLicenseService;
    @Value("${spring.application.name:null}")
    private String moduleId;

    private static final String LICENSE_ID = "fit2cloud_license";
    private static final String linuxValidatorUtil = "/usr/bin/validator_linux_amd64";
    private static final String macValidatorUtil = "/usr/local/bin/validator_darwin_amd64";
    private static final String product = "cmp";
    private static final String[] NO_PLU_LIMIT_MODULES = new String[]{"dashboard", "gateway"};

    public F2CLicenseResponse validateLicense(String product, String licenseKey){
        List<String> command = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        if(System.getProperty("os.name").contains("Mac")){
            command.add(macValidatorUtil);
        }else {
            command.add(linuxValidatorUtil);
        }
        command.add(licenseKey);
        try{
            execCommand(result, command);
            System.out.println(result.toString());
            F2CLicenseResponse f2CLicenseResponse = new Gson().fromJson(result.toString(), F2CLicenseResponse.class);
            if(f2CLicenseResponse.getStatus() != F2CLicenseResponse.Status.valid){
                return f2CLicenseResponse;
            }
            if(!StringUtils.equals(f2CLicenseResponse.getLicense().getProduct(), product)){
                f2CLicenseResponse.setStatus(F2CLicenseResponse.Status.invalid);
                f2CLicenseResponse.setLicense(null);
                f2CLicenseResponse.setMessage("The license is unavailable for this product.");
                return f2CLicenseResponse;
            }
            //检查每个模块的PLU限制

            if(!Arrays.asList(NO_PLU_LIMIT_MODULES).contains(moduleId)){
                AuthorizationUnit authorizationUnit= CommonBeanFactory.getBean(AuthorizationUnit.class);
                try{
                    authorizationUnit.calculateAssets(f2CLicenseResponse.getLicense().getCount());
                    return f2CLicenseResponse;
                }catch (Exception e){
                    f2CLicenseResponse.setStatus(F2CLicenseResponse.Status.invalid);
                    f2CLicenseResponse.setMessage(e.getMessage());
                }
            }
            return f2CLicenseResponse;
        }catch (Exception e){
            return F2CLicenseResponse.invalid(e.getMessage());
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

    public F2CLicenseResponse validateLicense() {
        try {
            String licenseKey = readLicense();
            return validateLicense(product, licenseKey);
        } catch (Exception e) {
            return F2CLicenseResponse.invalid(e.getMessage());
        }
    }

    public F2CLicenseResponse updateLicense(String product, String licenseKey) {
        // 验证license
        F2CLicenseResponse response = validateLicense(product, licenseKey);
        if (response.getStatus() != F2CLicenseResponse.Status.valid) {
            return response;
        }
        // 覆盖原license
        writeLicense(licenseKey);
        return response;
    }

    // 从数据库读取License
    private String readLicense() {
        License license = innerLicenseService.getLicense(LICENSE_ID);
        if (license == null) {
            F2CException.throwException("没有License记录");
        }
        if (StringUtils.isBlank(license.getLicense())) {
            F2CException.throwException("License 为空");
        }
        return license.getLicense();
    }

    // 创建或更新License
    private void writeLicense(String licenseKey) {
        if (StringUtils.isBlank(licenseKey)) {
            F2CException.throwException("License 为空");
        }
        License license = new License();
        license.setId(LICENSE_ID);
        license.setLicense(licenseKey);
        innerLicenseService.saveLicense(license);
    }
}
