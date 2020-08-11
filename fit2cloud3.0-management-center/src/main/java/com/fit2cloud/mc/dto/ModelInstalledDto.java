package com.fit2cloud.mc.dto;

import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelVersion;

import java.io.Serializable;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/6 12:03 下午
 * @Description: Please Write notes scientifically
 */
public class ModelInstalledDto implements Serializable {

    private ModelBasic modelBasic;

    private ModelVersion modelVersion;

    public ModelBasic getModelBasic() {
        return modelBasic;
    }

    public void setModelBasic(ModelBasic modelBasic) {
        this.modelBasic = modelBasic;
    }

    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(ModelVersion modelVersion) {
        this.modelVersion = modelVersion;
    }
}
