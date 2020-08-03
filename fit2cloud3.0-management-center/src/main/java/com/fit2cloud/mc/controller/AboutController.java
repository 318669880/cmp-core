package com.fit2cloud.mc.controller;


import com.fit2cloud.license.core.model.F2CLicenseResponse;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.service.AboutService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RequestMapping("/about")
@RestController
public class AboutController {

    @Resource
    private AboutService aboutService;

    @PostMapping("/license/update")
    @RequiresPermissions(PermissionConstants.LICENSE_UPDATE)
    public F2CLicenseResponse updateLicense(@RequestBody Map<String, String> map) {
        return aboutService.updateLicense(map.get("license"));
    }

    @PostMapping("/license/validate")
    @RequiresPermissions(PermissionConstants.LICENSE_READ)
    public F2CLicenseResponse validateLicense(@RequestBody Map<String, String> map) {
        return aboutService.validateLicense(map.get("license"));
    }

    @GetMapping("/build/version")
    @RequiresPermissions(PermissionConstants.LICENSE_READ)
    public Object getBuildVersion() {
        return aboutService.getBuildVersion();
    }
}
