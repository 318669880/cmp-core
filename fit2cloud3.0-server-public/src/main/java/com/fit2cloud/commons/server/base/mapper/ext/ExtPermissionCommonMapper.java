package com.fit2cloud.commons.server.base.mapper.ext;

import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Author: chunxing
 * Date: 2018/5/8  下午7:23
 * Description:
 */
public interface ExtPermissionCommonMapper {
    Set<String> getPermissionNamesByUserIdAndModuleId(@Param("userId") String userId, @Param("moduleId") String moduleId);
}
