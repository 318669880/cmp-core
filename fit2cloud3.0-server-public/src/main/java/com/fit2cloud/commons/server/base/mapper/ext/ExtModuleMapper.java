package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.base.domain.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtModuleMapper {
    List<Module> getLinkEnableModuleListByRoleList(@Param("roleIdList") List<String> roleIdList);

    List<Module> getModuleList();
}
