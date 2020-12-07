package com.fit2cloud.commons.server.base.mapper.ext;


import com.fit2cloud.commons.server.model.ProxyDTO;

import java.util.List;
import java.util.Map;

public interface ExtProxyMapper {

    List<ProxyDTO> selectProxys(Map<String, Object> params);
}
