package com.fit2cloud.commons.server.utils;

import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.server.base.domain.OrganizationExample;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class OrganizationUtils {


    private static OrganizationMapper organizationMapper;

    @Resource
    public void setOrganizationCommonMapper(OrganizationMapper organizationMapper) {
        OrganizationUtils.organizationMapper = organizationMapper;
    }

    public static List<String> getOrgIdsByOrgId(String orgId){
        List<String> results = new ArrayList<>();
        results.add(orgId);
        OrganizationExample example = new OrganizationExample();
        example.clear();
        example.createCriteria().andPidIsNotNull();
        List<Organization> organizations = organizationMapper.selectByExample(example);
        getOrgIds(organizations, orgId, results);
        return results;
    }
    private static void getOrgIds(List<Organization> organizations, String pid, List<String> reuslts){
        List<String> collect = organizations.stream().filter(org -> StringUtils.equals(pid, org.getPid())).map(org -> {
            String newPid = org.getId();
            getOrgIds(organizations, newPid, reuslts);
            return newPid;
        }).collect(Collectors.toList());
        reuslts.addAll(collect);
    }
}
