package com.fit2cloud.mc.dto;

import java.util.ArrayList;
import java.util.List;

public class ModuleParamData {
    private List<String> deployment = new ArrayList<>();
    private List<String> statefulset = new ArrayList<>();


    public List<String> getStatefulset() {
        return statefulset;
    }

    public void setStatefulset(List<String> statefulset) {
        this.statefulset = statefulset;
    }


    public List<String> getDeployment() {
        return deployment;
    }

    public void setDeployment(List<String> deployment) {
        this.deployment = deployment;
    }

    public void addDeployment(String deployment){
        this.deployment.add(deployment);
    }

    public void addStatefulset(String statefulset){
        this.statefulset.add(statefulset);
    }
}
