package com.fit2cloud.mc.strategy.service;

import com.fit2cloud.mc.strategy.entity.ResultInfo;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 1:46 下午
 * @Description: Please Write notes scientifically
 */
public class ResultService<T> {

    public ResultInfo<T> setResultSuccess (){
        return new ResultInfo<T>(true,null,"execute success");
    }

    public ResultInfo<T> setResultSuccess (T t){
        return new ResultInfo<T>(true,t,"execute success");
    }

    public ResultInfo<T> setResultSuccess(String message) {
        return new ResultInfo<T>(true,null,message);
    }

    public ResultInfo<T> setResultSuccess(T t,String message) {
        return new ResultInfo<T>(true,t,message);
    }


    public ResultInfo<T> setResultError (){
        return new ResultInfo<T>(false,null,"execute error");
    }

    public ResultInfo<T> setResultError (String message){
        return new ResultInfo<T>(false,null,message);
    }

}
