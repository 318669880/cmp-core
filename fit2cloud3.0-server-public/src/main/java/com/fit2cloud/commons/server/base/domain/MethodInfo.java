package com.fit2cloud.commons.server.base.domain;

import com.fit2cloud.commons.server.dcslock.annotation.DcsLock;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 3:18 下午
 * @Description: Please Write notes scientifically
 */
public class MethodInfo implements Serializable {

    private Method method;

    private Class aClass;

    private DcsLock dcsLock;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public DcsLock getDcsLock() {
        return dcsLock;
    }

    public void setDcsLock(DcsLock dcsLock) {
        this.dcsLock = dcsLock;
    }
}
