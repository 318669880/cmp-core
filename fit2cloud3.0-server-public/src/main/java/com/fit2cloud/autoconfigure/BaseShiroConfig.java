package com.fit2cloud.autoconfigure;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.fit2cloud.commons.server.constants.SessionConstants;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.security.CookieIdGenerator;
import com.fit2cloud.commons.server.security.SessionRedisDao;
import com.fit2cloud.commons.server.security.ShiroDBRealm;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Author: chunxing
 * Date: 2018/7/9  上午11:33
 * Description:
 */
@Configuration
@ConditionalOnProperty(
        value = {"shiro.enabled"},
        matchIfMissing = true
)
public class BaseShiroConfig {

    @Bean(name = "shiroFilter")
    public FilterRegistrationBean<Filter> shiroFilter(ShiroFilterFactoryBean shiroFilterFactoryBean) throws Exception {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter((Filter) Objects.requireNonNull(shiroFilterFactoryBean.getObject()));
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }

    @Bean
    public MemoryConstrainedCacheManager memoryConstrainedCacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    /**
     * securityManager 不用直接注入shiroDBRealm，可能会导致事务失效
     * 解决方法见 handleContextRefresh
     * http://www.debugrun.com/a/NKS9EJQ.html
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager(SessionManager sessionManager, MemoryConstrainedCacheManager memoryConstrainedCacheManager) {
        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
        dwsm.setSessionManager(sessionManager);
        dwsm.setCacheManager(memoryConstrainedCacheManager);
        return dwsm;
    }

    @Bean(name = "shiroDBRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    public ShiroDBRealm getShiroDBRealm() {
        return new ShiroDBRealm();
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultWebSecurityManager sessionManager) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(sessionManager);
        return new AuthorizationAttributeSourceAdvisor();
    }

    @Bean
    @DependsOn("globalConfigurations")
    public SessionManager sessionManager(SessionRedisDao sessionRedisDao, MemoryConstrainedCacheManager memoryConstrainedCacheManager) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        sessionManager.setGlobalSessionTimeout(SessionConstants.expired);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        SimpleCookie sessionIdCookie = new SimpleCookie();
        sessionManager.setSessionIdCookie(sessionIdCookie);
        sessionIdCookie.setPath(WebConstants.ROOT_PATH);
        sessionIdCookie.setName(GlobalConfigurations.getCookieName());
        sessionManager.setCacheManager(memoryConstrainedCacheManager);
        // release use redis
//        if (GlobalConfigurations.isReleaseMode()) {
//            sessionRedisDao.setSessionIdGenerator(new CookieIdGenerator());
//            sessionManager.setSessionDAO(sessionRedisDao);
//        } else {
//            ((MemorySessionDAO) sessionManager.getSessionDAO()).setSessionIdGenerator(new CookieIdGenerator());
//        }
        ((MemorySessionDAO) sessionManager.getSessionDAO()).setSessionIdGenerator(new CookieIdGenerator());
        return sessionManager;
    }

    @Bean(name = "shiroDialect")
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    /**
     * 等到ApplicationContext 加载完成之后 装配shiroRealm
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        ShiroDBRealm shiroDBRealm = (ShiroDBRealm) context.getBean("shiroDBRealm");
        ((DefaultWebSecurityManager) context.getBean("securityManager")).setRealm(shiroDBRealm);
    }
}
