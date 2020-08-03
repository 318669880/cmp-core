package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.model.CloudAccountDTO;
import com.fit2cloud.commons.server.model.CloudAccountImageSyncDTO;
import com.fit2cloud.commons.server.model.CloudAccountNetworkDTO;

import java.util.List;
import java.util.Map;

public interface ExtCloudAccountMapper {

    List<CloudAccountDTO> getAccountList(Map<String, Object> param);

    List<CloudAccountImageSyncDTO> getAccountListWithImageSync(Map<String, Object> param);

    List<CloudAccountNetworkDTO> getAccountListNetwork(Map<String, Object> param);
}
