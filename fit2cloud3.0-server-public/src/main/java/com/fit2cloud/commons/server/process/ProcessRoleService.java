package com.fit2cloud.commons.server.process;

import com.fit2cloud.commons.server.base.domain.FlowRole;
import com.fit2cloud.commons.server.base.domain.FlowRoleExample;
import com.fit2cloud.commons.server.base.domain.FlowRoleUser;
import com.fit2cloud.commons.server.base.domain.FlowRoleUserExample;
import com.fit2cloud.commons.server.base.mapper.FlowRoleMapper;
import com.fit2cloud.commons.server.base.mapper.FlowRoleUserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessRoleService {

    @Value("${spring.application.name:null}")
    private String moduleId;

    @Resource
    private FlowRoleMapper flowRoleMapper;

    @Resource
    private FlowRoleUserMapper flowRoleUserMapper;

    public List<FlowRole> listProcessRoles() {
        FlowRoleExample example = new FlowRoleExample();
        example.createCriteria().andModuleEqualTo(moduleId);
        return flowRoleMapper.selectByExample(example);
    }

    public int addRole(FlowRole role) {
        FlowRole exist = flowRoleMapper.selectByPrimaryKey(role.getRoleKey());
        if (exist != null) {
            throw new RuntimeException("Role ID is already occupied");
        }
        role.setModule(moduleId);
        return flowRoleMapper.insert(role);
    }

    public int updateRole(FlowRole role) {
        return flowRoleMapper.updateByPrimaryKeySelective(role);
    }

    public int deleteRole(String roleKey) {
        FlowRoleUserExample example = new FlowRoleUserExample();
        example.createCriteria().andRoleKeyEqualTo(roleKey);
        flowRoleUserMapper.deleteByExample(example);
        return flowRoleMapper.deleteByPrimaryKey(roleKey);
    }

    public List<FlowRoleUser> listRoleUsers(String roleKey) {
        FlowRoleUserExample example = new FlowRoleUserExample();
        example.createCriteria().andRoleKeyEqualTo(roleKey);
        return flowRoleUserMapper.selectByExample(example);
    }

    public int saveRoleUsers(List<String> ids, String roleKey) {
        FlowRoleUserExample example = new FlowRoleUserExample();
        example.createCriteria().andRoleKeyEqualTo(roleKey);
        flowRoleUserMapper.deleteByExample(example);
        for (String id : ids) {
            FlowRoleUser roleUser = new FlowRoleUser();
            roleUser.setRoleKey(roleKey);
            roleUser.setUserId(id);
            flowRoleUserMapper.insert(roleUser);
        }
        return ids.size();
    }
}
