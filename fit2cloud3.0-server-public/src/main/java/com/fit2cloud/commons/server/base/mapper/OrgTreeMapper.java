package com.fit2cloud.commons.server.base.mapper;

import com.fit2cloud.commons.server.model.OrgTreeNode;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/9/16 5:06 下午
 * @Description: Please Write notes scientifically
 */
@Mapper
public interface OrgTreeMapper {

    /**
     *
     *
     * @param excludeWs  是否排除workspace
     * @return
     */
    List<OrgTreeNode> nodes(@Param("excludeWs") boolean excludeWs);

    @Select({
        "select source_id, role_type, count(DISTINCT(user_id)) as nums \n" +
        "from \n" +
        "(select r.*,rt.role_type from user_role r left join (select id, CASE type WHEN 'system' THEN id ELSE parent_id END 'role_type' from role ) rt on \n" +
        "rt.id = r.role_id) rtr \n" +
        "WHERE source_id is not null group by source_id,role_type "
    })
    List<Map<String,Object>> relativeNum();
}
