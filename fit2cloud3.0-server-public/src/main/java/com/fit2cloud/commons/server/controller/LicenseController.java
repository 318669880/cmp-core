package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.license.DefaultLicenseService;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.commons.server.license.F2CLicenseResponse;
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
        F2CLicenseResponse f2CLicenseResponse = defaultLicenseService.validateLicense();

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
