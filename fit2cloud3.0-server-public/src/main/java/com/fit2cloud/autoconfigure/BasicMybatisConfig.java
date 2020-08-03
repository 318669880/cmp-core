package com.fit2cloud.autoconfigure;

import com.fit2cloud.commons.server.config.DBEncryptConfig;
import com.fit2cloud.commons.utils.DBEncryptInterceptor;
import com.fit2cloud.commons.utils.EncryptConfig;
import com.fit2cloud.commons.utils.LogUtil;
import com.github.pagehelper.PageInterceptor;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * spring-boot集成mybatis的基本入口
 * 1）创建数据源
 * 2）创建SqlSessionFactory
 */
@Configuration    //该注解类似于spring配置文件
@MapperScan(basePackages = "com.fit2cloud.commons.server.base.mapper", sqlSessionFactoryRef = "sqlSessionFactory")
@EnableTransactionManagement
public class BasicMybatisConfig {

    /**
     * 分页插件需要自己写，不然无法添加其他的plugins
     *
     * @return page helper
     */
    @Bean
    @ConditionalOnMissingBean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("pageSizeZero", "true");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public DBEncryptInterceptor dbEncryptInterceptor() {
        DBEncryptInterceptor dbEncryptInterceptor = new DBEncryptInterceptor();
        List<EncryptConfig> configList = new ArrayList<>();
        // 添加加密/解密配置
        configList.add(new EncryptConfig("com.fit2cloud.commons.server.base.domain.CloudAccount", "credential"));
        configList.add(new EncryptConfig("com.fit2cloud.commons.server.model.CloudAccountDTO", "credential"));
        configList.add(new EncryptConfig("com.fit2cloud.commons.server.base.domain.UserKey", "secretKey"));
        configList.add(new EncryptConfig("com.fit2cloud.commons.server.base.domain.CloudServerCredential", "password"));
        configList.add(new EncryptConfig("com.fit2cloud.commons.server.base.domain.CloudServerCredential", "secretKey"));
        configList.add(new EncryptConfig("com.fit2cloud.commons.server.base.domain.McStorageProvider", "credential"));
        configList.add(new EncryptConfig("com.fit2cloud.commons.server.base.domain.FileStore", "file", "com.fit2cloud.commons.utils.CompressUtils", "zip", "unzip"));
        dbEncryptInterceptor.setEncryptConfigList(configList);
        return dbEncryptInterceptor;
    }

    /**
     * 等到ApplicationContext 加载完成之后 装配DBEncryptInterceptor
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        try {
            ApplicationContext context = event.getApplicationContext();
            DBEncryptInterceptor dBEncryptInterceptor = context.getBean(DBEncryptInterceptor.class);
            Map<String, DBEncryptConfig> beansOfType = context.getBeansOfType(DBEncryptConfig.class);
            for (DBEncryptConfig config : beansOfType.values()) {
                if (!CollectionUtils.isEmpty(config.encryptConfig())) {
                    dBEncryptInterceptor.getEncryptConfigList().addAll(config.encryptConfig());
                }
            }
        } catch (Exception e) {
            LogUtil.error("装配子模块的数据库字段加密错误，错误：" + e.getMessage());
        }

    }
}