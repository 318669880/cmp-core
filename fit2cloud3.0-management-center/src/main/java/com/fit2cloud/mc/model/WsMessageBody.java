package com.fit2cloud.mc.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/11/11 2:22 下午
 * @Description: Please Write notes scientifically
 */
@Data
@Builder
public class WsMessageBody<T> implements Serializable {
    private String type;
    private T t;
}
