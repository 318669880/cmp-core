package com.fit2cloud.mc.dao;

import com.fit2cloud.mc.model.ModelNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModelNodeBatchMapper {

    int batchUpdate(@Param("nodes") List<ModelNode> nodes);
}
