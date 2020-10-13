package com.fit2cloud.commons.server.license;

import com.fit2cloud.commons.server.base.domain.FlowLinkValue;
import com.fit2cloud.commons.server.base.domain.FlowProcess;

public interface AuthorizationUnit {

   public void calculateAssets(Long PLU) throws Exception;
}
