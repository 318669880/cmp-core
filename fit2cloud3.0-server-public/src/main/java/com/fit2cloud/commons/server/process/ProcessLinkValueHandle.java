package com.fit2cloud.commons.server.process;

import com.fit2cloud.commons.server.base.domain.FlowLinkValue;
import com.fit2cloud.commons.server.base.domain.FlowProcess;

public interface ProcessLinkValueHandle {

   public Boolean execute(FlowProcess process, FlowLinkValue linkValue);
}
