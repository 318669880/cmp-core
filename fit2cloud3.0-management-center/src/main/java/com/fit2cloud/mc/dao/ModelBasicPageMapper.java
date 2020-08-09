package com.fit2cloud.mc.dao;

import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelInstall;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/7 7:07 下午
 * @Description: Please Write notes scientifically
 */
public interface ModelBasicPageMapper {

    List<ModelBasic> paging(@Param("map") Map<String, Object> map);


    List<ModelInstall> select(@Param("map") Map<String, Object> map);
}
