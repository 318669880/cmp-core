package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.Proxy;
import com.fit2cloud.commons.server.model.ProxyDTO;
import com.fit2cloud.commons.server.service.ProxyService;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "$[{i18n_proxy}]")
@RequestMapping("proxy")
@RestController
public class ProxyController {

    @Resource
    private ProxyService proxyService;


    @ApiOperation("$[{i18n_proxy_list}]")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager getProxys(@PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, proxyService.selectProxys());
    }

    @ApiOperation("$[{i18n_add_proxy}]")
    @PostMapping("create")
    public Proxy saveProxy(@RequestBody Proxy proxy) {
        return proxyService.saveProxy(proxy);
    }

    @ApiOperation("$[{i18n_edit_proxy}]")
    @PostMapping("update")
    public Proxy updateProxy(@RequestBody Proxy proxy) {
        return proxyService.saveProxy(proxy);
    }

    @ApiOperation("$[{i18n_delete_proxy}]")
    @PostMapping("delete")
    public void deleteProxy(@RequestBody String proxyId) {
        proxyService.deleteProxy(proxyId);
    }

    @PostMapping("listAll")
    public List<ProxyDTO> listAllProxy() {
        return proxyService.selectProxys();
    }


}
