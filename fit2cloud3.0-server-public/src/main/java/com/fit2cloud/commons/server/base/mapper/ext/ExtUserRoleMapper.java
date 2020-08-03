package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.model.UserRoleHelpDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserRoleMapper {

    List<UserRoleHelpDTO> getUserRoleHelpList(@Param("userId") String userId);

    List<String> getCustomRolesByUserId(@Param("userId") String userId);

}
