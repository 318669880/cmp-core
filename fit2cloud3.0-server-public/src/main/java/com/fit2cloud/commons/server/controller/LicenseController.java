package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.License;
import com.fit2cloud.commons.server.license.DefaultLicenseService;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.commons.server.license.F2CLicenseResponse;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(headers = "Accept=application/json")
public class LicenseController {
    @Resource
    private DefaultLicenseService defaultLicenseService;

    @GetMapping(value = "anonymous/license/validate")
    public ResultHolder validateLicense() throws Exception {
        License license = defaultLicenseService.readLicense();
        if(StringUtils.isEmpty(license.getF2cLicense())){
            throw new Exception("Invalid License.");
        }
        F2CLicenseResponse f2CLicenseResponse = new Gson().fromJson(license.getF2cLicense(), F2CLicenseResponse.class);
        switch (f2CLicenseResponse.getStatus()) {
            case valid:
                return ResultHolder.success(null);
            case expired:
                String expired = f2CLicenseResponse.getLicense().getExpired();
                throw new Exception("License has expired since " + expired + ", please update license.");
            case invalid:
                throw new Exception(f2CLicenseResponse.getMessage());
            default:
                throw new Exception("Invalid License.");
        }
    }
}
