package com.fit2cloud.autoconfigure;

import com.fit2cloud.commons.utils.GlobalConfigurations;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@AutoConfigureBefore(QuartzAutoConfiguration.class)
public class BasicDataSourceConfig {

    @Resource
    private Environment env; // 保存了配置文件的信息

    /**
     * 创建数据源
     * <p>
     * 该注解表示在同一个接口有多个实现类可以注入的时候，默认选择哪一个，而不是让@autowire注解报错
     * </p>
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    @DependsOn("globalConfigurations")
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser(env.getProperty("rdb.user"));
        dataSource.setDriverClass(env.getProperty("rdb.driver"));
        dataSource.setPassword(env.getProperty("rdb.password"));
        dataSource.setJdbcUrl(env.getProperty("rdb.url"));
        dataSource.setMaxIdleTime(30); // 最大空闲时间
        dataSource.setAcquireIncrement(5);// 增长数
        dataSource.setInitialPoolSize(5);// 初始连接数
        dataSource.setMinPoolSize(5); // 最小连接数
        int maxConnections = GlobalConfigurations.getProperty("rdb.max-connections", Integer.class, 200);
        String currentApplicationName = GlobalConfigurations.getProperty("spring.application.name", String.class, StringUtils.EMPTY);
        if (StringUtils.isNotBlank(currentApplicationName)) {
            maxConnections = GlobalConfigurations.getProperty("rdb.max-connections." + currentApplicationName, Integer.class, maxConnections);
        }
        dataSource.setMaxPoolSize(maxConnections); // 最大连接数
        dataSource.setAcquireRetryAttempts(30);// 获取连接重试次数
        dataSource.setIdleConnectionTestPeriod(60); // 每60s检查数据库空闲连接
        dataSource.setMaxStatements(0); // c3p0全局的PreparedStatements缓存的大小
        dataSource.setBreakAfterAcquireFailure(false);  // 获取连接失败将会引起所有等待连接池来获取连接的线程抛出异常。但是数据源仍有效保留，并在下次调用getConnection()的时候继续尝试获取连接。如果设为true，那么在尝试获取连接失败后该数据源将申明已断开并永久关闭。Default: false
        dataSource.setTestConnectionOnCheckout(false); // 在每个connection 提交是校验有效性
        dataSource.setTestConnectionOnCheckin(true); // 取得连接的同时将校验连接的有效性
        dataSource.setCheckoutTimeout(60000); // 从连接池获取连接的超时时间，如设为0则无限期等待。单位毫秒，默认为0
        dataSource.setPreferredTestQuery("SELECT 1");
        if (!GlobalConfigurations.isReleaseMode()) {
            dataSource.setDebugUnreturnedConnectionStackTraces(true);
            dataSource.setUnreturnedConnectionTimeout(3600);
        }
        return dataSource;
    }
}
