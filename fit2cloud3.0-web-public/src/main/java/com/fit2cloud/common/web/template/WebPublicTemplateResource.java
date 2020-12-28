package com.fit2cloud.common.web.template;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * Created by liqiang on 2018/6/12.
 */
public class WebPublicTemplateResource implements ITemplateResource {

    private final Resource resource;
    private final String characterEncoding = "UTF-8";

    public WebPublicTemplateResource(ApplicationContext applicationContext, String location) {
        Validate.notNull(applicationContext, "Application Context cannot be null");
        Validate.notEmpty(location, "Resource Location cannot be null or empty");
        if (!isSupportedBrowser()) {
            location = WebPublicConfig.WEB_PUBLIC_CONTEXT + "/not-support.html";
        }
        if (StringUtils.containsIgnoreCase(location, WebPublicConfig.WEB_PUBLIC_CONTEXT)) {
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            this.resource = patternResolver.getResource(WebPublicConfig.WEB_PUBLIC_CONTEXT + StringUtils.substringAfterLast(location, WebPublicConfig.WEB_PUBLIC_CONTEXT));
        } else {
            this.resource = applicationContext.getResource(location);
        }
    }

    public WebPublicTemplateResource(Resource resource, String characterEncoding) {
        Validate.notNull(resource, "Resource cannot be null");
        this.resource = resource;
    }

    public String getDescription() {
        return this.resource.getDescription();
    }

    public String getBaseName() {
        return computeBaseName(this.resource.getFilename());
    }

    public boolean exists() {
        return this.resource.exists();
    }

    public Reader reader() throws IOException {
        InputStream inputStream = this.resource.getInputStream();
        return !org.thymeleaf.util.StringUtils.isEmptyOrWhitespace(this.characterEncoding) ? new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding)) : new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
    }

    public ITemplateResource relative(String relativeLocation) {
        Resource relativeResource;
        try {
            relativeResource = this.resource.createRelative(relativeLocation);
        } catch (IOException var4) {
            return new WebPublicTemplateResource.SpringResourceInvalidRelativeTemplateResource(this.getDescription(), relativeLocation, var4);
        }

        return new WebPublicTemplateResource(relativeResource, this.characterEncoding);
    }

    static String computeBaseName(String path) {
        if (path != null && path.length() != 0) {
            String basePath = path.charAt(path.length() - 1) == 47 ? path.substring(0, path.length() - 1) : path;
            int slashPos = basePath.lastIndexOf(47);
            int dotPos;
            if (slashPos != -1) {
                dotPos = basePath.lastIndexOf(46);
                return dotPos != -1 && dotPos > slashPos + 1 ? basePath.substring(slashPos + 1, dotPos) : basePath.substring(slashPos + 1);
            } else {
                dotPos = basePath.lastIndexOf(46);
                return dotPos != -1 ? basePath.substring(0, dotPos) : (basePath.length() > 0 ? basePath : null);
            }
        } else {
            return null;
        }
    }

    private boolean isSupportedBrowser() {
        String userAgent = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            userAgent = request.getHeader("User-Agent");
            if (StringUtils.isBlank(userAgent)) {
                return true;
            }
            Browser browser = UserAgent.parseUserAgentString(userAgent).getBrowser();
            Version version = browser.getVersion(userAgent);
            if (StringUtils.containsIgnoreCase(browser.getName(), "Internet Explorer")) {
//                if (Integer.valueOf(version.getMajorVersion()) < 11) {
//                    return false;
//                }
                return false;
            }
            if (StringUtils.containsIgnoreCase(browser.getName(), "chrome")) {
                if (Integer.valueOf(version.getMajorVersion()) < 50) {
                    return false;
                }
            }
            if (StringUtils.containsIgnoreCase(browser.getName(), "firefox")) {
                if (Integer.valueOf(version.getMajorVersion()) < 44) {
                    return false;
                }
            }
            if (StringUtils.containsIgnoreCase(browser.getName(), "safari")) {
                if (Integer.valueOf(version.getMajorVersion()) < 10) {
                    return false;
                }
            }
        } catch (Throwable t) {
            // do nothing
        }
        return true;
    }

    private static final class SpringResourceInvalidRelativeTemplateResource implements ITemplateResource {
        private final String originalResourceDescription;
        private final String relativeLocation;
        private final IOException ioException;

        SpringResourceInvalidRelativeTemplateResource(String originalResourceDescription, String relativeLocation, IOException ioException) {
            this.originalResourceDescription = originalResourceDescription;
            this.relativeLocation = relativeLocation;
            this.ioException = ioException;
        }

        public String getDescription() {
            return "Invalid relative resource for relative location \"" + this.relativeLocation + "\" and original resource " + this.originalResourceDescription + ": " + this.ioException.getMessage();
        }

        public String getBaseName() {
            return "Invalid relative resource for relative location \"" + this.relativeLocation + "\" and original resource " + this.originalResourceDescription + ": " + this.ioException.getMessage();
        }

        public boolean exists() {
            return false;
        }

        public Reader reader() throws IOException {
            throw new IOException("Invalid relative resource", this.ioException);
        }

        public ITemplateResource relative(String relativeLocation) {
            return this;
        }

        public String toString() {
            return this.getDescription();
        }

    }
}