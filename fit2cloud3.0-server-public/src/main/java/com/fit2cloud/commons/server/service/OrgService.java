package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.server.base.domain.OrganizationExample;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    public List<Organization> getOrgTreeByFirstId(String id) {
        List<Organization> organizationList = new ArrayList<>();
        Organization organization = organizationMapper.selectByPrimaryKey(id);
        organizationList.add(organization);
        getTree(organizationList, organization);
        return organizationList;
    }

    private void getTree(List<Organization> organizationList, Organization organization) {
        OrganizationExample organizationExample = new OrganizationExample();
        organizationExample.createCriteria().andPidEqualTo(organization.getId());
        List<Organization> organizationList1 = organizationMapper.selectByExample(organizationExample);
        if (CollectionUtils.isNotEmpty(organizationList1)) {
            organizationList.addAll(organizationList1);
            for (Organization org : organizationList1) {
                getTree(organizationList, org);
            }
        }
    }
}
