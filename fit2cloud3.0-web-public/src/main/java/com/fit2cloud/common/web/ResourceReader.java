package com.fit2cloud.common.web;

import com.fit2cloud.autoconfigure.HttpCacheConfig;
import com.fit2cloud.common.web.template.WebPublicConfig;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import com.fit2cloud.commons.utils.MimeTypeUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class ResourceReader {

    private static final String ERROR_PATH = "web-public/error.html";

    private static SpringTemplateEngine springTemplateEngine;

    private static final Long TIMESTAMP = System.currentTimeMillis();

    static {
        synchronized (TIMESTAMP) {
            if (springTemplateEngine == null) {
                StringTemplateResolver templateResolver = new StringTemplateResolver();
                templateResolver.setTemplateMode(TemplateMode.HTML);
                SpringStandardDialect dialect = new SpringStandardDialect();
                dialect.setEnableSpringELCompiler(true);
                springTemplateEngine = new SpringTemplateEngine();
                springTemplateEngine.setDialect(dialect);
                springTemplateEngine.setEnableSpringELCompiler(true);
                springTemplateEngine.setTemplateResolver(templateResolver);
            }
        }
    }

    private static ConcurrentHashMap<String, String> fileContentType = new ConcurrentHashMap<>();

    public static void read(String path, HttpServletRequest request, HttpServletResponse response) {
        InputStream inputStream = null;
        String originalPath = path;
        try {
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            if (!patternResolver.getResource(path).exists()) {
                response.setStatus(404);
                path = ERROR_PATH;
            }
            inputStream = patternResolver.getResource(path).getInputStream();
            response.setCharacterEncoding("UTF-8");
            String contentType = fileContentType.get(path);
            if (contentType == null) {
                contentType = MimeTypeUtils.getMediaType(path, null);
                if (contentType != null) {
                    fileContentType.put(path, contentType);
                }
            }
            response.setContentType(contentType);
            if (GlobalConfigurations.isReleaseMode()) {
                HttpCacheConfig.setMaxAge(request, response, true);
            }

            if (StringUtils.equals(path, ERROR_PATH)) {
                Context context = new Context();
                context.setVariables(WebPublicConfig.getErrorVariables(originalPath, 404, "Not Found"));
                springTemplateEngine.process(IOUtils.toString(inputStream), context, response.getWriter());
            }else {
                IOUtils.copy(inputStream, response.getOutputStream());
            }

//            if (StringUtils.endsWithIgnoreCase(path, ".html") || StringUtils.endsWithIgnoreCase(path, ".htm")) {
//                Context context = new Context();
//                if (StringUtils.equals(path, ERROR_PATH)) {
//                    context.setVariables(WebPublicConfig.getErrorVariables(originalPath, 404, "Not Found"));
//                } else {
//                    context.setVariables(WebPublicConfig.getUiConfiguration());
//                }
//                springTemplateEngine.process(IOUtils.toString(inputStream), context, response.getWriter());
//            } else {
//                IOUtils.copy(inputStream, response.getOutputStream());
//            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}
