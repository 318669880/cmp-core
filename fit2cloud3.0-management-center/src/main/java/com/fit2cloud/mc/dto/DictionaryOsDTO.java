package com.fit2cloud.mc.dto;

import com.fit2cloud.commons.server.base.domain.Dictionary;

import java.util.ArrayList;
import java.util.List;

public class DictionaryOsDTO extends Dictionary {

    private List<Dictionary> versionList = new ArrayList<>();

    public List<Dictionary> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<Dictionary> versionList) {
        this.versionList = versionList;
    }
}
