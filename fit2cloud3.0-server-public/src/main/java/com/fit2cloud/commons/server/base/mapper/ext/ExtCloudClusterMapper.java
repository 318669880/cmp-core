package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.model.CloudClusterDTO;
import com.fit2cloud.commons.server.model.ClusterVmStatistics;
import com.fit2cloud.commons.server.model.request.CloudClusterRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudClusterMapper {
    List<CloudClusterDTO> search(@Param("request") CloudClusterRequest cloudClusterRequest);

    List<CloudClusterDTO> getHostStatisticsByAccountId(String accountId);

    List<ClusterVmStatistics> getVmStatisticsByAccountIdAndStatus(@Param("accountId") String accountId, @Param("status") String status);
}