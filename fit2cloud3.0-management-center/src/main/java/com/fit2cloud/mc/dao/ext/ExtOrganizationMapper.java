package com.fit2cloud.mc.dao.ext;

import com.fit2cloud.mc.dto.OrganizationDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Author: chunxing
 * Date: 2018/5/30  下午6:40
 * Description:
 */
public interface ExtOrganizationMapper {

    List<OrganizationDTO> paging(@Param("map") Map<String, Object> map);

    @Select("select id, pid from organization")
    List<Map<String,String>> ids();
}
