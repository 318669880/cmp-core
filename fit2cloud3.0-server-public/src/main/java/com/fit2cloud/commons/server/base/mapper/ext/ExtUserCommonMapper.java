package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.base.domain.UserExample;
import com.fit2cloud.commons.server.model.UserTooltip;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserCommonMapper {

    List<UserTooltip> getUserTooltip(@Param("idList") List<String> idList);

    List<UserTooltip> searchUser(UserExample userExample);
}
