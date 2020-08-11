package com.fit2cloud.mc.strategy.entity;

import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.ModelManager;

import java.io.Serializable;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 4:16 下午
 * @Description: Please Write notes scientifically
 */
public class ModuleMessageInfo implements Serializable {

    private ModelManager modelManager;

    private String methodName;

    private Object[] args;

    public ModelManager getModelManager() {
        return modelManager;
    }

    public void setModelManager(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public ModuleMessageInfo() {
    }

    public ModuleMessageInfo(ModelManager modelManager, String methodName, Object[] args) {
        this.modelManager = modelManager;
        this.methodName = methodName;
        this.args = args;
    }
}
