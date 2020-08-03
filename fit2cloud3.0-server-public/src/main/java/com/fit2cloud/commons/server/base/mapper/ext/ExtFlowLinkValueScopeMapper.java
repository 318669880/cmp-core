package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.base.domain.FlowLinkValueScope;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtFlowLinkValueScopeMapper {
    void batchInsert(@Param("flowLinkValueScopes") List<FlowLinkValueScope> flowLinkValueScopes);
}
