package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.Proxy;
import com.fit2cloud.commons.server.base.domain.ProxyExample;
import com.fit2cloud.commons.server.base.mapper.ProxyMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtProxyMapper;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.constants.ScopeConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.model.ProxyDTO;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProxyService {

    @Resource
    private ExtProxyMapper extProxyMapper;

    @Resource
    private ProxyMapper proxyMapper;

    public List<ProxyDTO> selectProxys() {
        Map params = new HashMap();
        SessionUser user = SessionUtils.getUser();
        if (user.getParentRoleId().equals(RoleConstants.Id.USER.name()) || user.getParentRoleId().equals(RoleConstants.Id.ORGADMIN.name())) {
            params.put("organizationId", user.getOrganizationId());
        }
        return extProxyMapper.selectProxys(params);
    }


    public Proxy saveProxy(Proxy proxy) {
        checkSave(proxy);
        if (proxy.getId() == null) {
            proxy.setId(UUIDUtil.newUUID());
            if (StringUtils.equalsIgnoreCase(SessionUtils.getUser().getParentRoleId(), RoleConstants.Id.ADMIN.name())) {
                proxy.setOrganizationId("ROOT");
                proxy.setScope(ScopeConstants.GLOBAL);

            } else {
                proxy.setScope(ScopeConstants.ORG);
                proxy.setOrganizationId(SessionUtils.getOrganizationId());
            }
            proxy.setCreatedTime(System.currentTimeMillis());
            proxyMapper.insert(proxy);
        } else {
            proxyMapper.updateByPrimaryKeySelective(proxy);
        }
        return proxy;
    }

    public Proxy getProxy(String proxyId) {
        return proxyMapper.selectByPrimaryKey(proxyId);
    }

    public void deleteProxy(String proxyId) {
        proxyMapper.deleteByPrimaryKey(proxyId);
    }

    private void checkSave(Proxy proxy) {
        ProxyExample proxyExample = new ProxyExample();
        if (proxy.getId() == null) {
            proxyExample.createCriteria().andIpEqualTo(proxy.getIp());
        } else {
            proxyExample.createCriteria().andIpEqualTo(proxy.getIp()).andIdNotEqualTo(proxy.getId());
        }
        List<Proxy> proxies = proxyMapper.selectByExample(proxyExample);
        if (proxies.size() != 0) {
            F2CException.throwException("Proxy:" + proxy.getIp() + " already exist！");
        }
    }

}
