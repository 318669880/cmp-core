package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("log/operation")
public class OperationLogController {
    @Resource
    private OperationLogService operationLogService;

    @RequestMapping("query/resource/{resourceId}/{goPage}/{pageSize}")
    public Pager queryResourceOperationLog(@PathVariable String resourceId, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, operationLogService.selectRersourceOperationLog(resourceId));
    }

    @RequestMapping("query/user/{userId}/{goPage}/{pageSize}")
    public Pager queryUserOperationLog(@PathVariable String userId, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, operationLogService.selectRersourceOperationLog(userId));
    }


    @RequestMapping("query/workspace/{workspaceId}/{goPage}/{pageSize}")
    public Pager queryWorkspaceOperationLog(@PathVariable String workspaceId, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, operationLogService.selectRersourceOperationLog(workspaceId));
    }


}
