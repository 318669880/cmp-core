package com.fit2cloud.commons.server.model;

/**
 * 定义了异步任务回调方法
 * @param <T> 返回结果类型
 */
public interface Callback<T> {

    public void onSuccess(T result);

    public void onFail(T result);

    public void onError(Exception e) throws Exception;

    public void onRunning(T result);
}
