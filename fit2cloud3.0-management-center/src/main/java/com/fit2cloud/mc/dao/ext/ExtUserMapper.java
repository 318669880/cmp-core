package com.fit2cloud.mc.dao.ext;

import com.fit2cloud.commons.server.base.domain.Workspace;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.mc.dto.RoleInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: chunxing
 * Date: 2018/5/21  下午12:50
 * Description:
 */
public interface ExtUserMapper {

    List<UserDTO> paging(@Param("map") Map<String, Object> map);

    Long countActivesUser(@Param("roleName") String roleName);

    List<RoleInfo> roleInfo(Map<String, Object> param);

    List<Workspace> resourcePaging(@Param("resourceType") String resourceType, @Param("userId") String userId, @Param("roleId") String roleId);

    Set<String> getIds();
}
