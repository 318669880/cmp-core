package com.fit2cloud.commons.server.utils;

import com.fit2cloud.commons.server.base.domain.UserRole;
import com.fit2cloud.commons.server.base.domain.UserRoleExample;
import com.fit2cloud.commons.server.base.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: chunxing
 * Date: 2018/5/29  上午11:15
 * Description:
 */
@Component
public class UserRoleUtils {

    private static UserRoleMapper userRoleMapper;

    @Autowired
    public void setUserRoleMapper(UserRoleMapper userRoleMapper) {
        UserRoleUtils.userRoleMapper = userRoleMapper;
    }

    public static Set<String> getResourceIds(String userId) {
        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<UserRole> userRoles = userRoleMapper.selectByExample(example);
        return userRoles.stream().map(UserRole::getSourceId).collect(Collectors.toSet());
    }


}
