package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.base.domain.OperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtOperationLogMapper {
    List<OperationLog> getActivityLog(@Param("map") Map<String, Object> param);

    List<String> getConditions(@Param("key") String key);
}
