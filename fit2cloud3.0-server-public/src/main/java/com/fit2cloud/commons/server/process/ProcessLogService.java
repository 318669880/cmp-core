package com.fit2cloud.commons.server.process;


import com.fit2cloud.commons.server.base.domain.FlowProcessLog;
import com.fit2cloud.commons.server.base.domain.FlowProcessLogExample;
import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.base.mapper.FlowProcessLogMapper;
import com.fit2cloud.commons.utils.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

@Service
public class ProcessLogService {

    @Resource
    private FlowProcessLogMapper flowProcessLogMapper;

    public List<FlowProcessLog> listProcessLog(String processId) {
        FlowProcessLogExample example = new FlowProcessLogExample();
        example.createCriteria().andProcessIdEqualTo(processId);
        example.setOrderByClause("id desc");
        return flowProcessLogMapper.selectByExampleWithBLOBs(example);
    }

    void saveProcessLog(FlowTask task) {
        FlowProcessLog processLog = new FlowProcessLog();
        BeanUtils.copyBean(processLog, task);
        flowProcessLogMapper.insert(processLog);
    }

}
