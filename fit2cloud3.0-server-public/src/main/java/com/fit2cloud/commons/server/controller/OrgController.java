package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.Organization;
import com.fit2cloud.commons.server.service.OrgService;
import com.fit2cloud.commons.server.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author gin
 * @Date 2020/11/24 2:08 下午
 */
@RestController
@RequestMapping("org")
public class OrgController {
    @Resource
    private TagService tagService;
    @Resource
    private OrgService orgService;

    @PostMapping("getOrgTree/{orgId}")
    public List<Organization> getOrgTree(@PathVariable String orgId) {
        List<String> orgTree = tagService.getOrgTree(orgId);
        List<Organization> organizationList = new ArrayList<>();
        for (String id : orgTree) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            Organization org = orgService.getOrg(id);
            organizationList.add(org);
        }
        Collections.reverse(organizationList);
        return organizationList;
    }
}
