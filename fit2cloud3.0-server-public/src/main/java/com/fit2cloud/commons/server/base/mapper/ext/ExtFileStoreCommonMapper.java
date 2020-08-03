package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.base.domain.FileStore;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Author: chunxing
 * Date: 2018/8/24  下午9:38
 * Description:
 */
public interface ExtFileStoreCommonMapper {


    List<FileStore> listFiles(@Param("businessKey") String businessKey);
}
