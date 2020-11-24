package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author gin
 * @Date 2020/11/24 2:09 下午
 */
@Service
public class OrgService {
    @Resource
    private OrganizationMapper organizationMapper;

    public Organization getOrg(String id) {
        return organizationMapper.selectByPrimaryKey(id);
    }
}
