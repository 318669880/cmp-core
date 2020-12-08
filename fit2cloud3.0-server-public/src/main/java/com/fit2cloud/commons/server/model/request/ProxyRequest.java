package com.fit2cloud.commons.server.model.request;

import com.fit2cloud.commons.server.base.domain.Proxy;
import lombok.Data;

/**
 * @Author gin
 * @Date 2020/12/8 2:17 下午
 */
@Data
public class ProxyRequest extends Proxy {
    private String sort;
}
