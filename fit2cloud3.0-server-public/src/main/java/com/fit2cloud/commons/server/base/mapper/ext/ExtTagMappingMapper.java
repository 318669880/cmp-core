package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.base.domain.TagMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtTagMappingMapper {
    void batchInsert(@Param("mappings") List<TagMapping> mappings);

    List<Map> selectTagValues(@Param("tagId") List<String> tagId);

    Map selectTagByValueId(@Param("id") String id);
}
