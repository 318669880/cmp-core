package com.fit2cloud.commons.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Properties内容注册器
 * <br>
 * 使用方法：
 * <p>
 * Spring配置文件中添加
 * <br>&lt;bean id="propertyConfigurer" class="com.zwzx.common.spring.PropertiesConfigurer"&gt;
 * <br>&lt;property name="ignoreResourceNotFound" value="true" /&gt;
 * <br>&lt;property name="locations"&gt;
 * <br>&lt;list&gt;
 * <br>&lt;value&gt;classpath*:properties/*.properties&lt;/value&gt;
 * <br>&lt;/list&gt;
 * <br>&lt;/property&gt;
 * <br>&lt;/bean&gt;
 *
 * @author kun.mo
 * @since JDK1.8
 */
public class PropertiesConfigurer extends PropertyPlaceholderConfigurer {

    private static Map<String, String> properties = new HashMap<>();

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        props.keySet().forEach(key -> {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            properties.put(keyStr, value);
        });
    }

    /**
     * 根据key获取String类型的值
     *
     * @param key key
     * @return key对应的值，取不到则返回null
     */
    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * 根据key获取String类型的值
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return key对应的值
     */
    public static String getProperty(String key, String defaultValue) {
        String value = properties.get(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 根据key获取String类型的值
     *
     * @param key key
     * @return key对应的值，取不到则返回null
     */
    public static String getString(String key) {
        return getString(key, null);
    }

    /**
     * 根据key获取String类型的值
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return key对应的值
     */
    public static String getString(String key, String defaultValue) {
        String value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 根据key获取Integer类型的值
     *
     * @param key key
     * @return key对应的值，取不到则返回null
     */
    public static Integer getInteger(String key) {
        return getInteger(key, null);
    }

    /**
     * 根据key获取Integer类型的值
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return key对应的值
     */
    public static Integer getInteger(String key, Integer defaultValue) {
        String value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Integer.valueOf(value);
    }

    /**
     * 根据key获取Long类型的值
     *
     * @param key key
     * @return key对应的值，取不到则返回null
     */
    public static Long getLong(String key) {
        return getLong(key, null);
    }

    /**
     * 根据key获取Long类型的值
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return key对应的值
     */
    public static Long getLong(String key, Long defaultValue) {
        String value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Long.valueOf(value);
    }

    /**
     * 根据key获取Boolean类型的值
     *
     * @param key key
     * @return key对应的值，取不到则返回null
     */
    public static Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    /**
     * 根据key获取Boolean类型的值
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return key对应的值
     */
    public static Boolean getBoolean(String key, Boolean defaultValue) {
        String value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.valueOf(value);
    }
}
