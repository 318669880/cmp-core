package com.fit2cloud.mc.strategy.entity;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 1:47 下午
 * @Description: Please Write notes scientifically
 */
public class ResultInfo<T> {

    private boolean success;

    private T data;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultInfo() {
    }

    public ResultInfo(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}
