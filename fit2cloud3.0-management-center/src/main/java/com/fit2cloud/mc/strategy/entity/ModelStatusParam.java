package com.fit2cloud.mc.strategy.entity;

import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.model.ModelNode;

import java.io.Serializable;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/14 11:51 上午
 * @Description: Please Write notes scientifically
 */
public class ModelStatusParam implements Serializable {

    private ModelNode modelNode;
    private String module;
    private ModuleStatusConstants status;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public ModuleStatusConstants getStatus() {
        return status;
    }

    public void setStatus(ModuleStatusConstants status) {
        this.status = status;
    }

    public ModelNode getModelNode() {
        return modelNode;
    }

    public void setModelNode(ModelNode modelNode) {
        this.modelNode = modelNode;
    }

    public ModelStatusParam(ModelNode modelNode, String module, ModuleStatusConstants status) {
        this.modelNode = modelNode;
        this.module = module;
        this.status = status;
    }
}
