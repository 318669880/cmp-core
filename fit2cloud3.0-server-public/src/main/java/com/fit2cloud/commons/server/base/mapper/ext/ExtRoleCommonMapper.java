package com.fit2cloud.commons.server.base.mapper.ext;

import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Author: chunxing
 * Date: 2018/5/8  下午4:09
 * Description:
 */
public interface ExtRoleCommonMapper {
    Set<String> getRoleNamesByUserId(@Param("userId") String userId);
}
