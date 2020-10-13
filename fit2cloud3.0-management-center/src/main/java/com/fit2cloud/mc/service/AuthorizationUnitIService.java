package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.license.AuthorizationUnit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthorizationUnitIService implements AuthorizationUnit {

    @Resource
    private WorkspaceService workspaceService;
    public void calculateAssets(Long PLU) throws Exception{
        if(workspaceService.countWorkSpace() > PLU * 15){
            throw new Exception(Translator.get("i18n_plu_limit"));
        };
    };

}
