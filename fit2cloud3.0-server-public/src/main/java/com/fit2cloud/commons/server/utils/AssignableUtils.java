package com.fit2cloud.commons.server.utils;

import com.fit2cloud.commons.server.process.ProcessEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AssignableUtils {

    private static final String RESOURCE_PATTERN = "/**/*.class";

    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    /**
     * 查找指定包路径下所有实现了targetType接口的实现类，不包括接口本身
     *
     * @param packages   包路径
     * @param targetType 接口类
     * @return 所有实现指定接口的类
     */
    public static Set<Class<?>> getClassSet(String[] packages, Class<?> targetType) throws IOException, ClassNotFoundException {
        Set<Class<?>> classSet = new HashSet<>();
        classSet.clear();
        for (String pkg : packages) {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    if (isAssignable(reader, readerFactory, targetType)) {
                        classSet.add(Class.forName(reader.getClassMetadata().getClassName()));
                    }
                }
            }
        }
        return classSet;
    }

    private static boolean isAssignable(MetadataReader reader, MetadataReaderFactory readerFactory, Class<?> targetType) throws IOException {
        String className = reader.getClassMetadata().getClassName();
        if (className.equals(targetType.getName())) return false;

        TypeFilter filter = new AssignableTypeFilter(targetType);
        return filter.match(reader, readerFactory);
    }

    // 例子
    public static void main(String[] args) {
        String[] packages = {"com.fit2cloud"};
        try {
            Set<Class<?>> classes = getClassSet(packages, ProcessEvent.class);
            classes.forEach(System.out::println);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

}
