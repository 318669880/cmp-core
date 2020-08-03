package com.fit2cloud.mc.dao.ext;

import com.fit2cloud.commons.server.base.domain.PluginWithBLOBs;

import java.util.List;
import java.util.Map;

public interface ExtPluginMapper {

    List<PluginWithBLOBs> getPluginList(Map<String, Object> param);
}
