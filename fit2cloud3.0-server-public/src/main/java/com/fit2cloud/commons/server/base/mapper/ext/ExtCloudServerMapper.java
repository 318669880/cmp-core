package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.model.CloudServerDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudServerMapper {
    List<CloudServerDTO> selectCloudServers(@Param("params") Map<String, Object> params);
}
