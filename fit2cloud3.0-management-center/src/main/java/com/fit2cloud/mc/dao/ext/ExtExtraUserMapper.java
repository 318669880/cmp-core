package com.fit2cloud.mc.dao.ext;

import com.fit2cloud.commons.server.base.domain.ExtraUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Author: chunxing
 * Date: 2018/6/28  下午2:07
 * Description:
 */
public interface ExtExtraUserMapper {
    List<ExtraUser> paging(@Param("map") Map<String, Object> map);
}
